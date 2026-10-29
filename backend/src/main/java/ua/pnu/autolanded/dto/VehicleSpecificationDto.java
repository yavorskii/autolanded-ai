package ua.pnu.autolanded.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.pnu.autolanded.entity.FuelType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleSpecificationDto {
    private String vin;
    private String make;
    private String model;
    private Integer year;
    private FuelType fuelType;
    private Integer engineCapacityCc;
    private Double batteryCapacityKwh;
    private String driveType;
    private String transmission;
    private String vehicleType;
}
