package ua.pnu.autolanded.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.pricing.defaults")
@Getter
@Setter
public class DefaultPricingProperties {
    private Double paintPricePerElement = 120.0;
    private Double laborRatePerHour = 25.0;
    private Double airbagRestorePrice = 450.0;
    private Double geometryBenchHourlyRate = 35.0;
    private Double brokerAndTerminalFee = 350.0;
    private Double certificationFee = 250.0;
    private Double oceanFreightDefault = 1200.0;
    private Double usLandTransportDefault = 450.0;
    private Double destinationHandlingFee = 950.0;
}
