package ua.pnu.autolanded.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.pnu.autolanded.dto.PricingRatesDto;
import ua.pnu.autolanded.dto.RepairEstimateRequestDto;
import ua.pnu.autolanded.dto.RepairEstimateResultDto;
import ua.pnu.autolanded.entity.CarPart;
import ua.pnu.autolanded.entity.DamageZone;
import ua.pnu.autolanded.repository.CarPartRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PartCatalogServiceTest {

    @Mock
    private CarPartRepository carPartRepository;

    @Mock
    private PricingConfigService pricingConfigService;

    @Mock
    private EpcResolverService epcResolverService;

    private PartCatalogService partCatalogService;

    @BeforeEach
    void setUp() {
        partCatalogService = new PartCatalogService(carPartRepository, pricingConfigService, epcResolverService);

        PricingRatesDto defaultRates = PricingRatesDto.builder()
                .paintPricePerElement(120.0)
                .laborRatePerHour(25.0)
                .airbagRestorePrice(450.0)
                .build();

        when(pricingConfigService.resolveEffectiveRates(any())).thenReturn(defaultRates);
    }

    @Test
    void shouldEstimateRepairForTeslaModelXDamageZones() {
        CarPart bumper = CarPart.builder()
                .make("Tesla").model("Model X")
                .damageZone(DamageZone.FRONT_BUMPER)
                .partNameUa("Передній бампер у зборі")
                .partNameEn("Front Bumper")
                .oemPartNumber("1083983-00-A")
                .diagramPosition(1)
                .taiwanNewPriceUsd(450.0)
                .usedOriginalInColorPriceUsd(650.0)
                .usedOriginalToPaintPriceUsd(400.0)
                .laborHoursToReplace(2.0)
                .requiresPaint(true)
                .build();

        CarPart headlight = CarPart.builder()
                .make("Tesla").model("Model X")
                .damageZone(DamageZone.RIGHT_HEADLIGHT)
                .partNameUa("Фара передня права Full LED")
                .partNameEn("Headlamp Right")
                .oemPartNumber("1034389-00-C")
                .diagramPosition(3)
                .taiwanNewPriceUsd(650.0)
                .usedOriginalInColorPriceUsd(850.0)
                .usedOriginalToPaintPriceUsd(800.0)
                .laborHoursToReplace(1.0)
                .requiresPaint(false)
                .build();

        when(carPartRepository.findFirstByMakeIgnoreCaseAndModelIgnoreCaseAndDamageZone(eq("Tesla"), eq("Model X"), eq(DamageZone.FRONT_BUMPER)))
                .thenReturn(Optional.of(bumper));
        when(carPartRepository.findFirstByMakeIgnoreCaseAndModelIgnoreCaseAndDamageZone(eq("Tesla"), eq("Model X"), eq(DamageZone.RIGHT_HEADLIGHT)))
                .thenReturn(Optional.of(headlight));

        RepairEstimateRequestDto request = RepairEstimateRequestDto.builder()
                .make("Tesla")
                .model("Model X")
                .year(2020)
                .damagedZones(List.of(DamageZone.FRONT_BUMPER, DamageZone.RIGHT_HEADLIGHT))
                .airbagsDeployed(false)
                .build();

        RepairEstimateResultDto result = partCatalogService.estimateRepair(request);

        assertNotNull(result);
        assertEquals(2, result.getTotalDamagedZonesCount());
        assertTrue(result.getRealisticTotalRepairCostUsd() > 0);
        assertTrue(result.getOptimisticTotalRepairCostUsd() < result.getRealisticTotalRepairCostUsd());
        assertTrue(result.getRealisticTotalRepairCostUsd() < result.getPessimisticTotalRepairCostUsd());
        assertEquals("1083983-00-A", result.getItems().get(0).getOemPartNumber());
        assertEquals(3, result.getItems().get(0).getStrategyOptions().size());
    }
}
