package ua.pnu.autolanded.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomsCalculationResultDto {
    private Double customsValueUsd;
    private Double importDutyUsd;
    private Double exciseTaxUsd;
    private Double exciseTaxEur;
    private Double vatUsd;
    private Double pensionFundFeeUsd;
    private Double brokerAndTerminalFeeUsd;
    private Double certificationFeeUsd;
    private Double totalCustomsAndClearanceCostUsd;
    private Double totalLandedBeforeRepairUsd;
    private String legalBasisFormula;
    private boolean isElectricBenefitApplied;
}
