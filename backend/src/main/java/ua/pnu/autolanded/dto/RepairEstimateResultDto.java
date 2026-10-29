package ua.pnu.autolanded.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepairEstimateResultDto {
    private String make;
    private String model;
    private Integer totalDamagedZonesCount;
    private Double optimisticTotalRepairCostUsd;
    private Double realisticTotalRepairCostUsd;
    private Double pessimisticTotalRepairCostUsd;
    private Double totalPartsCostUsd;
    private Double totalLaborCostUsd;
    private Double totalPaintCostUsd;
    private Double riskBufferCostUsd;
    private List<CarPartInspectionResultDto> items;
}
