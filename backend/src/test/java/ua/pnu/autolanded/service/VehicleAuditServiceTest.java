package ua.pnu.autolanded.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.pnu.autolanded.dto.*;
import ua.pnu.autolanded.entity.AuditVerdict;
import ua.pnu.autolanded.entity.DamageSeverity;
import ua.pnu.autolanded.entity.DamageZone;
import ua.pnu.autolanded.entity.FuelType;
import ua.pnu.autolanded.entity.VehicleAuditReport;
import ua.pnu.autolanded.repository.UserRepository;
import ua.pnu.autolanded.repository.VehicleAuditReportRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehicleAuditServiceTest {

    @Mock
    private VinDecoderService vinDecoderService;

    @Mock
    private GeminiVisionService geminiVisionService;

    @Mock
    private CustomsCalculationService customsCalculationService;

    @Mock
    private PartCatalogService partCatalogService;

    @Mock
    private MarketPricingService marketPricingService;

    @Mock
    private VehicleAuditReportRepository reportRepository;

    @Mock
    private UserRepository userRepository;

    private VehicleAuditService vehicleAuditService;

    @BeforeEach
    void setUp() {
        vehicleAuditService = new VehicleAuditService(
                vinDecoderService,
                geminiVisionService,
                customsCalculationService,
                partCatalogService,
                marketPricingService,
                reportRepository,
                userRepository,
                new ObjectMapper()
        );
    }

    @Test
    void shouldExecuteFullAuditOnTeslaModelXAndRecommendStrongBuy() {
        VehicleSpecificationDto spec = VehicleSpecificationDto.builder()
                .vin("5YJXCDE23LF272040")
                .make("Tesla")
                .model("Model X")
                .year(2020)
                .fuelType(FuelType.ELECTRIC)
                .batteryCapacityKwh(100.0)
                .build();

        AiInspectionResultDto aiResult = AiInspectionResultDto.builder()
                .overallSeverity(DamageSeverity.MODERATE)
                .airbagsDeployed(false)
                .isTotalLossRisk(false)
                .detectedZones(List.of(DamageZone.FRONT_BUMPER, DamageZone.RIGHT_HEADLIGHT, DamageZone.RIGHT_FENDER))
                .detailedFindings(List.of())
                .modelSpecificRisks(List.of("Особливість Tesla: контур охолодження батареї"))
                .inspectionSummaryUa("Локалізований удар справа")
                .build();

        CustomsCalculationResultDto customsResult = CustomsCalculationResultDto.builder()
                .customsValueUsd(8700.0)
                .importDutyUsd(0.0)
                .exciseTaxEur(100.0)
                .exciseTaxUsd(108.0)
                .vatUsd(0.0)
                .totalCustomsAndClearanceCostUsd(708.0)
                .totalLandedBeforeRepairUsd(10358.0)
                .isElectricBenefitApplied(true)
                .build();

        RepairEstimateResultDto repairResult = RepairEstimateResultDto.builder()
                .make("Tesla")
                .model("Model X")
                .totalDamagedZonesCount(3)
                .optimisticTotalRepairCostUsd(1800.0)
                .realisticTotalRepairCostUsd(2300.0)
                .pessimisticTotalRepairCostUsd(3100.0)
                .totalPartsCostUsd(1800.0)
                .totalLaborCostUsd(300.0)
                .totalPaintCostUsd(200.0)
                .riskBufferCostUsd(300.0)
                .items(List.of())
                .build();

        MarketValuationDto marketValuation = MarketValuationDto.builder()
                .make("Tesla")
                .model("Model X")
                .bottomMarketPriceUsd(31500.0)
                .medianMarketPriceUsd(36000.0)
                .topMarketPriceUsd(41500.0)
                .activeListingsCount(38)
                .autoRiaQueryUrl("https://auto.ria.com/test")
                .build();

        when(vinDecoderService.decodeVin("5YJXCDE23LF272040")).thenReturn(spec);
        when(geminiVisionService.inspectVehicle(any())).thenReturn(aiResult);
        when(customsCalculationService.calculate(any())).thenReturn(customsResult);
        when(partCatalogService.estimateRepair(any())).thenReturn(repairResult);
        when(marketPricingService.resolveMarketComparables("Tesla", "Model X", 2020)).thenReturn(marketValuation);
        when(reportRepository.save(any(VehicleAuditReport.class))).thenAnswer(invocation -> {
            VehicleAuditReport r = invocation.getArgument(0);
            r.setId(101L);
            return r;
        });

        FullVehicleAuditRequestDto request = FullVehicleAuditRequestDto.builder()
                .vin("5YJXCDE23LF272040")
                .auctionPriceUsd(7050.0)
                .isFloodVehicle(false)
                .build();

        FullVehicleAuditReportDto report = vehicleAuditService.runFullAudit(request);

        assertNotNull(report);
        assertEquals(101L, report.getId());
        assertEquals("5YJXCDE23LF272040", report.getVin());
        assertEquals(AuditVerdict.STRONG_BUY, report.getVerdict());
        assertTrue(report.getNetProfitUsd() > 15000.0);
        assertTrue(report.getRoiPercentage() > 100.0);
        assertTrue(report.getTotalLandedTurnkeyUsd() < 15000.0);
    }
}
