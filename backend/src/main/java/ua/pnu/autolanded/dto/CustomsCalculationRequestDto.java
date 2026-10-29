package ua.pnu.autolanded.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.pnu.autolanded.entity.FuelType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomsCalculationRequestDto {

    @NotNull
    @Positive
    private Double auctionPriceUsd;

    @NotNull
    private Integer vehicleYear;

    @NotNull
    private FuelType fuelType;

    private Integer engineCapacityCc;

    private Double batteryCapacityKwh;

    @Builder.Default
    private Double usLandTransportCostUsd = 450.0;

    @Builder.Default
    private Double oceanFreightCostUsd = 1200.0;

    @Builder.Default
    private Double destinationHandlingCostUsd = 950.0;

    @Builder.Default
    private Double eurToUsdRate = 1.08;

    private Long userId;
}
