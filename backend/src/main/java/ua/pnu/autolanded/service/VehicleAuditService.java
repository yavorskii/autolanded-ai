package ua.pnu.autolanded.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.pnu.autolanded.dto.*;
import ua.pnu.autolanded.entity.AuditVerdict;
import ua.pnu.autolanded.entity.User;
import ua.pnu.autolanded.entity.VehicleAuditReport;
import ua.pnu.autolanded.repository.UserRepository;
import ua.pnu.autolanded.repository.VehicleAuditReportRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleAuditService {

    private final VinDecoderService vinDecoderService;
    private final GeminiVisionService geminiVisionService;
    private final CustomsCalculationService customsCalculationService;
    private final PartCatalogService partCatalogService;
    private final MarketPricingService marketPricingService;
    private final VehicleAuditReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public FullVehicleAuditReportDto runFullAudit(FullVehicleAuditRequestDto request) {
        VehicleSpecificationDto spec = vinDecoderService.decodeVin(request.getVin());

        AiInspectionRequestDto aiRequest = AiInspectionRequestDto.builder()
                .make(spec.getMake())
                .model(spec.getModel())
                .year(spec.getYear())
                .fuelType(spec.getFuelType())
                .isFloodVehicle(request.isFloodVehicle())
                .imageUrls(request.getImageUrls())
                .build();

        AiInspectionResultDto aiResult = geminiVisionService.inspectVehicle(aiRequest);

        CustomsCalculationRequestDto customsRequest = CustomsCalculationRequestDto.builder()
                .auctionPriceUsd(request.getAuctionPriceUsd())
                .vehicleYear(spec.getYear())
                .fuelType(spec.getFuelType())
                .engineCapacityCc(spec.getEngineCapacityCc())
                .batteryCapacityKwh(spec.getBatteryCapacityKwh())
                .userId(request.getUserId())
                .build();

        CustomsCalculationResultDto customsResult = customsCalculationService.calculate(customsRequest);

        RepairEstimateRequestDto repairRequest = RepairEstimateRequestDto.builder()
                .make(spec.getMake())
                .model(spec.getModel())
                .year(spec.getYear())
                .damagedZones(aiResult.getDetectedZones())
                .airbagsDeployed(aiResult.isAirbagsDeployed())
                .userId(request.getUserId())
                .build();

        RepairEstimateResultDto repairResult = partCatalogService.estimateRepair(repairRequest);

        MarketValuationDto marketValuation = marketPricingService.resolveMarketComparables(
                spec.getMake(), spec.getModel(), spec.getYear()
        );

        double totalLandedTurnkey = round(customsResult.getTotalLandedBeforeRepairUsd() + repairResult.getRealisticTotalRepairCostUsd());
        double netProfit = round(marketValuation.getBottomMarketPriceUsd() - totalLandedTurnkey);
        double roi = totalLandedTurnkey > 0 ? round((netProfit / totalLandedTurnkey) * 100.0) : 0.0;

        AuditVerdict verdict = resolveVerdict(aiResult, netProfit, roi);
        String summary = generateExecutiveSummary(spec, totalLandedTurnkey, netProfit, roi, verdict);

        VehicleAuditReport entity = VehicleAuditReport.builder()
                .user(resolveUser(request.getUserId()))
                .vin(spec.getVin())
                .make(spec.getMake())
                .model(spec.getModel())
                .year(spec.getYear())
                .fuelType(spec.getFuelType())
                .auctionPriceUsd(request.getAuctionPriceUsd())
                .totalCustomsAndClearanceUsd(customsResult.getTotalCustomsAndClearanceCostUsd())
                .optimisticRepairCostUsd(repairResult.getOptimisticTotalRepairCostUsd())
                .realisticRepairCostUsd(repairResult.getRealisticTotalRepairCostUsd())
                .pessimisticRepairCostUsd(repairResult.getPessimisticTotalRepairCostUsd())
                .totalLandedTurnkeyUsd(totalLandedTurnkey)
                .ukraineMarketBottomPriceUsd(marketValuation.getBottomMarketPriceUsd())
                .ukraineMarketMedianPriceUsd(marketValuation.getMedianMarketPriceUsd())
                .ukraineMarketTopPriceUsd(marketValuation.getTopMarketPriceUsd())
                .netProfitUsd(netProfit)
                .roiPercentage(roi)
                .verdict(verdict)
                .summaryTextUa(summary)
                .payloadJson(serializePayload(spec, aiResult, customsResult, repairResult, marketValuation))
                .build();

        VehicleAuditReport saved = reportRepository.save(entity);

        return FullVehicleAuditReportDto.builder()
                .id(saved.getId())
                .vin(spec.getVin())
                .specification(spec)
                .aiInspection(aiResult)
                .customs(customsResult)
                .repairEstimate(repairResult)
                .marketValuation(marketValuation)
                .totalLandedTurnkeyUsd(totalLandedTurnkey)
                .netProfitUsd(netProfit)
                .roiPercentage(roi)
                .verdict(verdict)
                .executiveSummaryUa(summary)
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public List<VehicleAuditReport> getUserReports(Long userId) {
        if (userId != null) {
            return reportRepository.findByUserIdOrderByCreatedAtDesc(userId);
        }
        return reportRepository.findTop10ByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public VehicleAuditReport getReportById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Audit report not found with ID: " + id));
    }

    private User resolveUser(Long userId) {
        if (userId == null) return null;
        return userRepository.findById(userId).orElse(null);
    }

    private AuditVerdict resolveVerdict(AiInspectionResultDto ai, double netProfit, double roi) {
        if (ai.isTotalLossRisk()) {
            return AuditVerdict.TOTAL_LOSS;
        }
        if (netProfit >= 4000.0 && roi >= 20.0) {
            return AuditVerdict.STRONG_BUY;
        }
        if (netProfit >= 1500.0 && roi >= 10.0) {
            return AuditVerdict.MODERATE_BUY;
        }
        if (netProfit >= 0.0) {
            return AuditVerdict.CONSIDER;
        }
        return AuditVerdict.RISKY;
    }

    private String generateExecutiveSummary(
            VehicleSpecificationDto spec,
            double totalLanded,
            double netProfit,
            double roi,
            AuditVerdict verdict) {

        if (verdict == AuditVerdict.TOTAL_LOSS) {
            return "КРИТИЧНИЙ РИЗИК: Лот класифіковано як TOTAL LOSS. Відновлення технічно недоцільне через загрозу високовольтній системі або геометрії кузова.";
        }

        if (verdict == AuditVerdict.STRONG_BUY) {
            return String.format("РЕКОМЕНДОВАНО ДО КУПІВЛІ (STRONG BUY): Бюджет 'під ключ на номерах' становить $%.0f. " +
                            "Очікуваний чистий прибуток порівняно з низом ринку України: +$%.0f (ROI: %.1f%%). Висока інвестиційна привабливість.",
                    totalLanded, netProfit, roi);
        }

        return String.format("Бюджет 'під ключ': $%.0f. Прогнозований фінансовий результат: $%.0f. Вердикт: %s.",
                totalLanded, netProfit, verdict.name());
    }

    private String serializePayload(Object... objects) {
        try {
            return objectMapper.writeValueAsString(objects);
        } catch (Exception e) {
            return "{}";
        }
    }

    private double round(double val) {
        return Math.round(val * 100.0) / 100.0;
    }
}
