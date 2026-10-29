package ua.pnu.autolanded.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FullVehicleAuditRequestDto {

    @NotBlank
    private String vin;

    @NotNull
    @Positive
    private Double auctionPriceUsd;

    @Builder.Default
    private boolean isFloodVehicle = false;

    private List<String> imageUrls;

    private Long userId;
}
