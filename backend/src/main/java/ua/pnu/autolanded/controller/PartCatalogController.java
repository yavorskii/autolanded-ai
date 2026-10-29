package ua.pnu.autolanded.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.pnu.autolanded.dto.RepairEstimateRequestDto;
import ua.pnu.autolanded.dto.RepairEstimateResultDto;
import ua.pnu.autolanded.entity.CarPart;
import ua.pnu.autolanded.repository.CarPartRepository;
import ua.pnu.autolanded.service.EpcResolverService;
import ua.pnu.autolanded.service.PartCatalogService;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/catalog")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PartCatalogController {

    private final PartCatalogService partCatalogService;
    private final CarPartRepository carPartRepository;
    private final EpcResolverService epcResolverService;

    @PostMapping("/estimate")
    public ResponseEntity<RepairEstimateResultDto> estimateRepair(
            @Valid @RequestBody RepairEstimateRequestDto request) {
        return ResponseEntity.ok(partCatalogService.estimateRepair(request));
    }

    @GetMapping("/epc-link")
    public ResponseEntity<Map<String, String>> getEpcLink(
            @RequestParam String make,
            @RequestParam String model,
            @RequestParam(required = false) String vin) {
        String url = epcResolverService.resolveCatalogUrl(make, model, vin);
        return ResponseEntity.ok(Map.of("catalogUrl", url, "make", make, "model", model));
    }

    @GetMapping("/parts")
    public ResponseEntity<List<CarPart>> getParts(
            @RequestParam String make,
            @RequestParam String model) {
        return ResponseEntity.ok(carPartRepository.findByMakeIgnoreCaseAndModelIgnoreCase(make, model));
    }
}
