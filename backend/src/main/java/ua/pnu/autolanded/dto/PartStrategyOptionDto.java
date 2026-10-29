package ua.pnu.autolanded.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartStrategyOptionDto {
    private String strategyCode;
    private String titleUa;
    private Double partPriceUsd;
    private Double paintCostUsd;
    private Double laborCostUsd;
    private Double totalItemCostUsd;
    private boolean isPaintIncluded;
    private boolean isRecommended;
}
