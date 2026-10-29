package ua.pnu.autolanded.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
public class RepairEstimateRequestDto {

    @NotBlank
    private String make;

    @NotBlank
    private String model;

    private Integer year;

    private String vehicleColor;

    @NotEmpty
    private List<DamageZone> damagedZones;

    @Builder.Default
    private boolean airbagsDeployed = false;

    @Builder.Default
    private boolean frontSuspensionDamaged = false;

    private Long userId;
}
