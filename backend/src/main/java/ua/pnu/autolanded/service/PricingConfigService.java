package ua.pnu.autolanded.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.pnu.autolanded.config.DefaultPricingProperties;
import ua.pnu.autolanded.dto.PricingRatesDto;
import ua.pnu.autolanded.entity.User;
import ua.pnu.autolanded.entity.UserPricingConfig;
import ua.pnu.autolanded.repository.PricingConfigRepository;
import ua.pnu.autolanded.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class PricingConfigService {

    private final PricingConfigRepository pricingConfigRepository;
    private final UserRepository userRepository;
    private final DefaultPricingProperties defaultProperties;

    @Transactional(readOnly = true)
    public PricingRatesDto resolveEffectiveRates(Long userId) {
        if (userId == null) {
            return buildDefaultRates();
        }

        return pricingConfigRepository.findByUserId(userId)
                .map(this::mergeWithDefaults)
                .orElseGet(this::buildDefaultRates);
    }

    @Transactional
    public PricingRatesDto updateUserPricing(Long userId, PricingRatesDto customRates) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        UserPricingConfig config = pricingConfigRepository.findByUserId(userId)
                .orElse(UserPricingConfig.builder().user(user).build());

        config.setPaintPricePerElement(customRates.getPaintPricePerElement());
        config.setLaborRatePerHour(customRates.getLaborRatePerHour());
        config.setAirbagRestorePrice(customRates.getAirbagRestorePrice());
        config.setGeometryBenchHourlyRate(customRates.getGeometryBenchHourlyRate());
        config.setBrokerAndTerminalFee(customRates.getBrokerAndTerminalFee());
        config.setCertificationFee(customRates.getCertificationFee());
        config.setOceanFreightCustomRate(customRates.getOceanFreightRate());
        config.setUsLandTransportCustomRate(customRates.getUsLandTransportRate());

        UserPricingConfig saved = pricingConfigRepository.save(config);
        return mergeWithDefaults(saved);
    }

    @Transactional
    public PricingRatesDto resetToDefaults(Long userId) {
        pricingConfigRepository.findByUserId(userId).ifPresent(pricingConfigRepository::delete);
        return buildDefaultRates();
    }

    private PricingRatesDto buildDefaultRates() {
        return PricingRatesDto.builder()
                .paintPricePerElement(defaultProperties.getPaintPricePerElement())
                .laborRatePerHour(defaultProperties.getLaborRatePerHour())
                .airbagRestorePrice(defaultProperties.getAirbagRestorePrice())
                .geometryBenchHourlyRate(defaultProperties.getGeometryBenchHourlyRate())
                .brokerAndTerminalFee(defaultProperties.getBrokerAndTerminalFee())
                .certificationFee(defaultProperties.getCertificationFee())
                .oceanFreightRate(defaultProperties.getOceanFreightDefault())
                .usLandTransportRate(defaultProperties.getUsLandTransportDefault())
                .destinationHandlingFee(defaultProperties.getDestinationHandlingFee())
                .isCustomUserConfig(false)
                .build();
    }

    private PricingRatesDto mergeWithDefaults(UserPricingConfig custom) {
        return PricingRatesDto.builder()
                .paintPricePerElement(custom.getPaintPricePerElement() != null ? custom.getPaintPricePerElement() : defaultProperties.getPaintPricePerElement())
                .laborRatePerHour(custom.getLaborRatePerHour() != null ? custom.getLaborRatePerHour() : defaultProperties.getLaborRatePerHour())
                .airbagRestorePrice(custom.getAirbagRestorePrice() != null ? custom.getAirbagRestorePrice() : defaultProperties.getAirbagRestorePrice())
                .geometryBenchHourlyRate(custom.getGeometryBenchHourlyRate() != null ? custom.getGeometryBenchHourlyRate() : defaultProperties.getGeometryBenchHourlyRate())
                .brokerAndTerminalFee(custom.getBrokerAndTerminalFee() != null ? custom.getBrokerAndTerminalFee() : defaultProperties.getBrokerAndTerminalFee())
                .certificationFee(custom.getCertificationFee() != null ? custom.getCertificationFee() : defaultProperties.getCertificationFee())
                .oceanFreightRate(custom.getOceanFreightCustomRate() != null ? custom.getOceanFreightCustomRate() : defaultProperties.getOceanFreightDefault())
                .usLandTransportRate(custom.getUsLandTransportCustomRate() != null ? custom.getUsLandTransportCustomRate() : defaultProperties.getUsLandTransportDefault())
                .destinationHandlingFee(defaultProperties.getDestinationHandlingFee())
                .isCustomUserConfig(true)
                .build();
    }
}
