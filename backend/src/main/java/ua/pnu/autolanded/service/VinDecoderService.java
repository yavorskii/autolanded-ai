package ua.pnu.autolanded.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ua.pnu.autolanded.dto.VehicleSpecificationDto;
import ua.pnu.autolanded.entity.FuelType;

@Service
@RequiredArgsConstructor
public class VinDecoderService {

    private final ObjectMapper objectMapper;
    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://vpic.nhtsa.dot.gov/api/vehicles")
            .build();

    public VehicleSpecificationDto decodeVin(String vin) {
        if (vin == null || vin.trim().length() != 17) {
            throw new IllegalArgumentException("Invalid VIN format. Must be 17 characters.");
        }

        String normalizedVin = vin.trim().toUpperCase();

        try {
            String response = restClient.get()
                    .uri("/decodevinvalues/{vin}?format=json", normalizedVin)
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(response);
            JsonNode results = root.path("Results");

            if (results.isArray() && !results.isEmpty()) {
                JsonNode carData = results.get(0);
                return mapToSpecification(normalizedVin, carData);
            }
        } catch (Exception ignored) {
        }

        return fallbackDecode(normalizedVin);
    }

    private VehicleSpecificationDto mapToSpecification(String vin, JsonNode data) {
        String make = data.path("Make").asText("Unknown");
        String model = data.path("Model").asText("Unknown");
        int year = data.path("ModelYear").asInt(2020);
        String rawFuel = data.path("FuelTypePrimary").asText("");
        String rawDisplacement = data.path("DisplacementCC").asText("");
        String driveType = data.path("DriveType").asText("AWD");

        FuelType fuelType = mapFuelType(rawFuel, make, model);
        Integer engineCapacity = parseDisplacement(rawDisplacement, fuelType);
        Double batteryKwh = estimateBatteryCapacity(make, model, fuelType, year);

        return VehicleSpecificationDto.builder()
                .vin(vin)
                .make(make)
                .model(model)
                .year(year)
                .fuelType(fuelType)
                .engineCapacityCc(engineCapacity)
                .batteryCapacityKwh(batteryKwh)
                .driveType(driveType)
                .transmission("Automatic")
                .vehicleType(data.path("VehicleType").asText("Passenger Car"))
                .build();
    }

    private FuelType mapFuelType(String rawFuel, String make, String model) {
        String lower = rawFuel.toLowerCase();
        if ("tesla".equalsIgnoreCase(make) || lower.contains("electric")) {
            return FuelType.ELECTRIC;
        }
        if (lower.contains("diesel")) {
            return FuelType.DIESEL;
        }
        if (lower.contains("plug-in")) {
            return FuelType.PLUGIN_HYBRID;
        }
        if (lower.contains("hybrid")) {
            return FuelType.HYBRID;
        }
        return FuelType.PETROL;
    }

    private Integer parseDisplacement(String raw, FuelType fuelType) {
        if (fuelType == FuelType.ELECTRIC) {
            return 0;
        }
        try {
            double parsed = Double.parseDouble(raw);
            return (int) Math.round(parsed);
        } catch (Exception e) {
            return 2000;
        }
    }

    private Double estimateBatteryCapacity(String make, String model, FuelType fuelType, int year) {
        if (fuelType != FuelType.ELECTRIC && fuelType != FuelType.PLUGIN_HYBRID) {
            return 0.0;
        }

        String lowerModel = model.toLowerCase();
        if ("tesla".equalsIgnoreCase(make)) {
            if (lowerModel.contains("model x") || lowerModel.contains("model s")) {
                return 100.0;
            }
            if (lowerModel.contains("model 3") || lowerModel.contains("model y")) {
                return 75.0;
            }
        }
        if ("bmw".equalsIgnoreCase(make) && lowerModel.contains("i3")) {
            return year >= 2019 ? 42.2 : 33.2;
        }
        if ("nissan".equalsIgnoreCase(make) && lowerModel.contains("leaf")) {
            return year >= 2018 ? 40.0 : 24.0;
        }

        return 75.0;
    }

    private VehicleSpecificationDto fallbackDecode(String vin) {
        if (vin.startsWith("5YJ")) {
            return VehicleSpecificationDto.builder()
                    .vin(vin)
                    .make("Tesla")
                    .model("Model X")
                    .year(2020)
                    .fuelType(FuelType.ELECTRIC)
                    .engineCapacityCc(0)
                    .batteryCapacityKwh(100.0)
                    .driveType("AWD")
                    .transmission("Single-speed fixed gear")
                    .vehicleType("SUV")
                    .build();
        }

        return VehicleSpecificationDto.builder()
                .vin(vin)
                .make("Generic")
                .model("Vehicle")
                .year(2020)
                .fuelType(FuelType.PETROL)
                .engineCapacityCc(2000)
                .batteryCapacityKwh(0.0)
                .driveType("FWD")
                .transmission("Automatic")
                .vehicleType("Passenger Car")
                .build();
    }
}
