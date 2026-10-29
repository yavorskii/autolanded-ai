package ua.pnu.autolanded.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.pnu.autolanded.entity.DamageZone;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarPartInspectionResultDto {
    private DamageZone damageZone;
    private String partNameUa;
    private String oemPartNumber;
    private Integer diagramPosition;
    private String diagramImageUrl;
    private String avtoProSearchUrl;
    private String allegroSearchUrl;
    private List<PartStrategyOptionDto> strategyOptions;
    private Double selectedStrategyCostUsd;
}
