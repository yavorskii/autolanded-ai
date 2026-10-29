package ua.pnu.autolanded.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.pnu.autolanded.entity.FuelType;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiInspectionRequestDto {
    private String make;
    private String model;
    private Integer year;
    private FuelType fuelType;
    private String auctionNotes;
    private boolean isFloodVehicle;
    private List<String> imageUrls;
    private List<String> base64Images;
}
