package ua.pnu.autolanded.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.pnu.autolanded.dto.AiInspectionRequestDto;
import ua.pnu.autolanded.dto.AiInspectionResultDto;
import ua.pnu.autolanded.entity.DamageSeverity;
import ua.pnu.autolanded.entity.DamageZone;
import ua.pnu.autolanded.entity.FuelType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GeminiVisionServiceTest {

    private GeminiVisionService geminiVisionService;

    @BeforeEach
    void setUp() {
        geminiVisionService = new GeminiVisionService(new ObjectMapper());
    }

    @Test
    void shouldTriggerTotalLossWhenElectricVehicleIsFlooded() {
        AiInspectionRequestDto request = AiInspectionRequestDto.builder()
                .make("Tesla")
                .model("Model X")
                .fuelType(FuelType.ELECTRIC)
                .isFloodVehicle(true)
                .build();

        AiInspectionResultDto result = geminiVisionService.inspectVehicle(request);

        assertNotNull(result);
        assertTrue(result.isTotalLossRisk());
        assertEquals(DamageSeverity.TOTAL_LOSS, result.getOverallSeverity());
        assertFalse(result.getModelSpecificRisks().isEmpty());
    }

    @Test
    void shouldInspectTeslaModelXAndIdentifyCoolingLoopRisks() {
        AiInspectionRequestDto request = AiInspectionRequestDto.builder()
                .make("Tesla")
                .model("Model X")
                .year(2020)
                .fuelType(FuelType.ELECTRIC)
                .isFloodVehicle(false)
                .imageUrls(List.of("https://example.com/front.jpg"))
                .build();

        AiInspectionResultDto result = geminiVisionService.inspectVehicle(request);

        assertNotNull(result);
        assertFalse(result.isTotalLossRisk());
        assertFalse(result.isAirbagsDeployed());
        assertTrue(result.getDetectedZones().contains(DamageZone.FRONT_BUMPER));
        assertTrue(result.getDetectedZones().contains(DamageZone.RIGHT_HEADLIGHT));
        assertTrue(result.getModelSpecificRisks().stream().anyMatch(r -> r.contains("охолодження")));
    }
}
