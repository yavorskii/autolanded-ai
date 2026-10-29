package ua.pnu.autolanded.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.pnu.autolanded.dto.PricingRatesDto;
import ua.pnu.autolanded.service.PricingConfigService;

@RestController
@RequestMapping("/api/v1/pricing")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PricingConfigController {

    private final PricingConfigService pricingConfigService;

    @GetMapping("/effective")
    public ResponseEntity<PricingRatesDto> getEffectiveRates(@RequestParam(required = false) Long userId) {
        return ResponseEntity.ok(pricingConfigService.resolveEffectiveRates(userId));
    }

    @PutMapping("/my-rates")
    public ResponseEntity<PricingRatesDto> updateMyRates(
            @RequestParam Long userId,
            @RequestBody PricingRatesDto customRates) {
        return ResponseEntity.ok(pricingConfigService.updateUserPricing(userId, customRates));
    }

    @PostMapping("/reset-defaults")
    public ResponseEntity<PricingRatesDto> resetToDefaults(@RequestParam Long userId) {
        return ResponseEntity.ok(pricingConfigService.resetToDefaults(userId));
    }
}
