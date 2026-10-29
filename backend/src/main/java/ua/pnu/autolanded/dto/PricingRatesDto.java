package ua.pnu.autolanded.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricingRatesDto {
    private Double paintPricePerElement;
    private Double laborRatePerHour;
    private Double airbagRestorePrice;
    private Double geometryBenchHourlyRate;
    private Double brokerAndTerminalFee;
    private Double certificationFee;
    private Double oceanFreightRate;
    private Double usLandTransportRate;
    private Double destinationHandlingFee;
    private boolean isCustomUserConfig;
}
