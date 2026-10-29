package ua.pnu.autolanded.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.pnu.autolanded.entity.DamageSeverity;
import ua.pnu.autolanded.entity.DamageZone;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiDamageZoneFindingDto {
    private DamageZone damageZone;
    private DamageSeverity severity;
    private Double confidenceScore;
    private String suggestedAction;
    private String observationsUa;
}
