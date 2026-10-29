package ua.pnu.autolanded.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.pnu.autolanded.entity.DamageSeverity;
import ua.pnu.autolanded.entity.DamageZone;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiInspectionResultDto {
    private DamageSeverity overallSeverity;
    private boolean airbagsDeployed;
    private boolean isTotalLossRisk;
    private List<DamageZone> detectedZones;
    private List<AiDamageZoneFindingDto> detailedFindings;
    private List<String> modelSpecificRisks;
    private String inspectionSummaryUa;
    private boolean isSimulatedOfflineFallback;
}
