package ua.pnu.autolanded.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ua.pnu.autolanded.dto.AiDamageZoneFindingDto;
import ua.pnu.autolanded.dto.AiInspectionRequestDto;
import ua.pnu.autolanded.dto.AiInspectionResultDto;
import ua.pnu.autolanded.entity.DamageSeverity;
import ua.pnu.autolanded.entity.DamageZone;
import ua.pnu.autolanded.entity.FuelType;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GeminiVisionService {

    private final ObjectMapper objectMapper;

    @Value("${app.gemini.api-key:}")
    private String geminiApiKey;

    public AiInspectionResultDto inspectVehicle(AiInspectionRequestDto request) {
        if (request.isFloodVehicle() && (request.getFuelType() == FuelType.ELECTRIC || request.getFuelType() == FuelType.PLUGIN_HYBRID)) {
            return buildFloodTotalLossResult(request);
        }

        if (geminiApiKey != null && !geminiApiKey.isBlank() && request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            try {
                return callGeminiApi(request);
            } catch (Exception ignored) {
            }
        }

        return buildOfflineDemoInspection(request);
    }

    private AiInspectionResultDto callGeminiApi(AiInspectionRequestDto request) throws Exception {
        RestClient client = RestClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com")
                .build();

        String prompt = "You are an automotive salvage insurance damage estimator. Inspect the car photos. " +
                "Respond in valid JSON with fields: " +
                "overallSeverity (MINOR, MODERATE, SEVERE, TOTAL_LOSS), " +
                "airbagsDeployed (boolean), " +
                "detectedZones (array of FRONT_BUMPER, HOOD, RIGHT_FENDER, LEFT_FENDER, RIGHT_HEADLIGHT, LEFT_HEADLIGHT, RADIATOR_SUPPORT, COOLING_PUMP, AIRBAG_KIT), " +
                "summaryUa (string in Ukrainian).";

        String requestBody = objectMapper.writeValueAsString(objectMapper.createObjectNode()
                .set("contents", objectMapper.createArrayNode().add(objectMapper.createObjectNode()
                        .set("parts", objectMapper.createArrayNode().add(objectMapper.createObjectNode()
                                .put("text", prompt))))));

        String rawResponse = client.post()
                .uri("/v1beta/models/gemini-1.5-flash:generateContent?key={key}", geminiApiKey)
                .header("Content-Type", "application/json")
                .body(requestBody)
                .retrieve()
                .body(String.class);

        JsonNode root = objectMapper.readTree(rawResponse);
        String textOutput = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();

        return parseGeminiOutput(textOutput, request);
    }

    private AiInspectionResultDto parseGeminiOutput(String text, AiInspectionRequestDto request) {
        try {
            int jsonStart = text.indexOf('{');
            int jsonEnd = text.lastIndexOf('}');
            if (jsonStart >= 0 && jsonEnd > jsonStart) {
                String jsonPart = text.substring(jsonStart, jsonEnd + 1);
                JsonNode parsed = objectMapper.readTree(jsonPart);

                List<DamageZone> zones = new ArrayList<>();
                if (parsed.has("detectedZones")) {
                    for (JsonNode z : parsed.get("detectedZones")) {
                        try {
                            zones.add(DamageZone.valueOf(z.asText()));
                        } catch (Exception ignored) {
                        }
                    }
                }

                boolean airbags = parsed.path("airbagsDeployed").asBoolean(false);
                DamageSeverity severity = DamageSeverity.valueOf(parsed.path("overallSeverity").asText("MODERATE"));
                String summary = parsed.path("summaryUa").asText("Аналіз завершено за допомогою Gemini Vision.");

                return buildResultFromZones(request, zones, airbags, severity, summary, false);
            }
        } catch (Exception ignored) {
        }
        return buildOfflineDemoInspection(request);
    }

    public AiInspectionResultDto buildOfflineDemoInspection(AiInspectionRequestDto request) {
        List<DamageZone> zones = List.of(
                DamageZone.FRONT_BUMPER,
                DamageZone.RIGHT_HEADLIGHT,
                DamageZone.RIGHT_FENDER,
                DamageZone.HOOD,
                DamageZone.COOLING_PUMP
        );

        return buildResultFromZones(
                request,
                zones,
                false,
                DamageSeverity.MODERATE,
                "Локалізоване фронтально-бокове зіткнення з правого боку. Силовий каркас салону та фронтальні подушки безпеки цілі. Пошкоджені навісні елементи.",
                true
        );
    }

    private AiInspectionResultDto buildResultFromZones(
            AiInspectionRequestDto request,
            List<DamageZone> zones,
            boolean airbags,
            DamageSeverity severity,
            String summary,
            boolean isFallback) {

        List<AiDamageZoneFindingDto> findings = new ArrayList<>();
        for (DamageZone zone : zones) {
            findings.add(AiDamageZoneFindingDto.builder()
                    .damageZone(zone)
                    .severity(severity)
                    .confidenceScore(0.92)
                    .suggestedAction(zone == DamageZone.HOOD ? "REPAIR_OR_REPLACE" : "REPLACE")
                    .observationsUa("Виявлено деформацію та порушення зазорів площини: " + zone.name())
                    .build());
        }

        List<String> modelRisks = new ArrayList<>();
        if ("Tesla".equalsIgnoreCase(request.getMake())) {
            modelRisks.add("Особливість Tesla Model X/S: У передньому правому крилі розташовано контур охолодження батареї. Рекомендовано перевірку герметичності патрубків.");
            modelRisks.add("Передній радар Autopilot: Необхідна повторна юстировка датчиків після заміни бампера.");
        }

        if ("BMW".equalsIgnoreCase(request.getMake()) && "i3".equalsIgnoreCase(request.getModel())) {
            modelRisks.add("Особливість BMW i3: Карбоновий монокок (Life Module). Необхідна перевірка цілісності вуглепластикових волокон кріплення підрамника.");
        }

        return AiInspectionResultDto.builder()
                .overallSeverity(severity)
                .airbagsDeployed(airbags)
                .isTotalLossRisk(false)
                .detectedZones(zones)
                .detailedFindings(findings)
                .modelSpecificRisks(modelRisks)
                .inspectionSummaryUa(summary)
                .isSimulatedOfflineFallback(isFallback)
                .build();
    }

    private AiInspectionResultDto buildFloodTotalLossResult(AiInspectionRequestDto request) {
        return AiInspectionResultDto.builder()
                .overallSeverity(DamageSeverity.TOTAL_LOSS)
                .airbagsDeployed(false)
                .isTotalLossRisk(true)
                .detectedZones(List.of())
                .detailedFindings(List.of())
                .modelSpecificRisks(List.of(
                        "КРИТИЧНЕ ЗАСТЕРЕЖЕННЯ: Статус Flood (затоплення) для електромобіля несе ризик короткого замикання та деградації модулів батареї.",
                        "Висока ймовірність відмови інвертора та корозії високовольтної проводки.",
                        "Вердикт: НЕДОЦІЛЬНО ДО КУПІВЛІ (TOTAL LOSS)."
                ))
                .inspectionSummaryUa("Лот класифіковано як непридатний до відновлення через затоплення високовольтної архітектури.")
                .isSimulatedOfflineFallback(false)
                .build();
    }
}
