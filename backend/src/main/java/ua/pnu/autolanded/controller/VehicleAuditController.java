package ua.pnu.autolanded.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.pnu.autolanded.dto.FullVehicleAuditReportDto;
import ua.pnu.autolanded.dto.FullVehicleAuditRequestDto;
import ua.pnu.autolanded.entity.VehicleAuditReport;
import ua.pnu.autolanded.service.VehicleAuditService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VehicleAuditController {

    private final VehicleAuditService vehicleAuditService;

    @PostMapping("/run")
    public ResponseEntity<FullVehicleAuditReportDto> runAudit(
            @Valid @RequestBody FullVehicleAuditRequestDto request) {
        return ResponseEntity.ok(vehicleAuditService.runFullAudit(request));
    }

    @GetMapping("/demo-tesla-x")
    public ResponseEntity<FullVehicleAuditReportDto> runDemoTeslaXAudit() {
        FullVehicleAuditRequestDto demoRequest = FullVehicleAuditRequestDto.builder()
                .vin("5YJXCDE23LF272040")
                .auctionPriceUsd(7050.0)
                .isFloodVehicle(false)
                .imageUrls(List.of("/demo/tesla_model_x_1.jpg", "/demo/tesla_model_x_2.jpg"))
                .build();
        return ResponseEntity.ok(vehicleAuditService.runFullAudit(demoRequest));
    }

    @GetMapping("/history")
    public ResponseEntity<List<VehicleAuditReport>> getAuditHistory(@RequestParam(required = false) Long userId) {
        return ResponseEntity.ok(vehicleAuditService.getUserReports(userId));
    }

    @GetMapping("/reports/{id}")
    public ResponseEntity<VehicleAuditReport> getReportById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleAuditService.getReportById(id));
    }
}
