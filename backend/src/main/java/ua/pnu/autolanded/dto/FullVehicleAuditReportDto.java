package ua.pnu.autolanded.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.pnu.autolanded.entity.AuditVerdict;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FullVehicleAuditReportDto {
    private Long id;
    private String vin;
    private VehicleSpecificationDto specification;
    private AiInspectionResultDto aiInspection;
    private CustomsCalculationResultDto customs;
    private RepairEstimateResultDto repairEstimate;
    private MarketValuationDto marketValuation;

    private Double totalLandedTurnkeyUsd;
    private Double netProfitUsd;
    private Double roiPercentage;
    private AuditVerdict verdict;
    private String executiveSummaryUa;
    private LocalDateTime createdAt;
}
