package ua.pnu.autolanded.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketValuationDto {
    private String make;
    private String model;
    private Integer yearFrom;
    private Integer yearTo;
    private Double bottomMarketPriceUsd;
    private Double medianMarketPriceUsd;
    private Double topMarketPriceUsd;
    private Integer activeListingsCount;
    private String autoRiaQueryUrl;
    private String analyticalNoteUa;
}
