package ua.pnu.autolanded.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.pnu.autolanded.dto.CustomsCalculationRequestDto;
import ua.pnu.autolanded.dto.CustomsCalculationResultDto;
import ua.pnu.autolanded.dto.PricingRatesDto;
import ua.pnu.autolanded.entity.FuelType;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomsCalculationServiceTest {

    @Mock
    private PricingConfigService pricingConfigService;

    private CustomsCalculationService customsCalculationService;

    @BeforeEach
    void setUp() {
        customsCalculationService = new CustomsCalculationService(pricingConfigService);

        PricingRatesDto defaultRates = PricingRatesDto.builder()
                .oceanFreightRate(1200.0)
                .usLandTransportRate(450.0)
                .destinationHandlingFee(950.0)
                .brokerAndTerminalFee(350.0)
                .certificationFee(250.0)
                .build();

        when(pricingConfigService.resolveEffectiveRates(any())).thenReturn(defaultRates);
    }

    @Test
    void shouldCalculateElectricVehicleWithExemptions() {
        CustomsCalculationRequestDto request = CustomsCalculationRequestDto.builder()
                .auctionPriceUsd(7050.0)
                .vehicleYear(2020)
                .fuelType(FuelType.ELECTRIC)
                .batteryCapacityKwh(100.0)
                .eurToUsdRate(1.08)
                .build();

        CustomsCalculationResultDto result = customsCalculationService.calculate(request);

        assertNotNull(result);
        assertTrue(result.isElectricBenefitApplied());
        assertEquals(0.0, result.getImportDutyUsd());
        assertEquals(0.0, result.getVatUsd());
        assertEquals(100.0, result.getExciseTaxEur());
        assertEquals(108.0, result.getExciseTaxUsd());
        assertEquals(8700.0, result.getCustomsValueUsd());
        assertEquals(708.0, result.getTotalCustomsAndClearanceCostUsd());
    }

    @Test
    void shouldCalculateInternalCombustionEngineCustoms() {
        CustomsCalculationRequestDto request = CustomsCalculationRequestDto.builder()
                .auctionPriceUsd(5000.0)
                .vehicleYear(2020)
                .fuelType(FuelType.PETROL)
                .engineCapacityCc(2000)
                .eurToUsdRate(1.08)
                .build();

        CustomsCalculationResultDto result = customsCalculationService.calculate(request);

        assertNotNull(result);
        assertFalse(result.isElectricBenefitApplied());
        assertTrue(result.getImportDutyUsd() > 0);
        assertTrue(result.getVatUsd() > 0);
        assertTrue(result.getExciseTaxEur() > 0);
    }
}
