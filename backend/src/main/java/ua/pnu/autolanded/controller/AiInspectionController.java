package ua.pnu.autolanded.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.pnu.autolanded.dto.AiInspectionRequestDto;
import ua.pnu.autolanded.dto.AiInspectionResultDto;
import ua.pnu.autolanded.entity.FuelType;
import ua.pnu.autolanded.service.GeminiVisionService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AiInspectionController {

    private final GeminiVisionService geminiVisionService;

    @PostMapping("/inspect")
    public ResponseEntity<AiInspectionResultDto> inspectVehicle(@RequestBody AiInspectionRequestDto request) {
        return ResponseEntity.ok(geminiVisionService.inspectVehicle(request));
    }

    @GetMapping("/demo-tesla-x")
    public ResponseEntity<AiInspectionResultDto> getDemoTeslaXInspection() {
        AiInspectionRequestDto request = AiInspectionRequestDto.builder()
                .make("Tesla")
                .model("Model X")
                .year(2020)
                .fuelType(FuelType.ELECTRIC)
                .isFloodVehicle(false)
                .imageUrls(List.of("/demo/tesla_model_x_1.jpg", "/demo/tesla_model_x_2.jpg"))
                .build();
        return ResponseEntity.ok(geminiVisionService.inspectVehicle(request));
    }
}
