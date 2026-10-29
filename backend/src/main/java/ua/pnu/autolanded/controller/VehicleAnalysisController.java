package ua.pnu.autolanded.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.pnu.autolanded.dto.CustomsCalculationRequestDto;
import ua.pnu.autolanded.dto.CustomsCalculationResultDto;
import ua.pnu.autolanded.dto.VehicleSpecificationDto;
import ua.pnu.autolanded.service.CustomsCalculationService;
import ua.pnu.autolanded.service.VinDecoderService;

@RestController
@RequestMapping("/api/v1/vehicle")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VehicleAnalysisController {

    private final VinDecoderService vinDecoderService;
    private final CustomsCalculationService customsCalculationService;

    @GetMapping("/decode-vin/{vin}")
    public ResponseEntity<VehicleSpecificationDto> decodeVin(@PathVariable String vin) {
        return ResponseEntity.ok(vinDecoderService.decodeVin(vin));
    }

    @PostMapping("/calculate-customs")
    public ResponseEntity<CustomsCalculationResultDto> calculateCustoms(
            @Valid @RequestBody CustomsCalculationRequestDto request) {
        return ResponseEntity.ok(customsCalculationService.calculate(request));
    }
}
