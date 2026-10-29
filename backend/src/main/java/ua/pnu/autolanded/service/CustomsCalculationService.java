package ua.pnu.autolanded.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.pnu.autolanded.dto.CustomsCalculationRequestDto;
import ua.pnu.autolanded.dto.CustomsCalculationResultDto;
import ua.pnu.autolanded.dto.PricingRatesDto;
import ua.pnu.autolanded.entity.FuelType;
import java.time.Year;

@Service
@RequiredArgsConstructor
public class CustomsCalculationService {

    private final PricingConfigService pricingConfigService;

    public CustomsCalculationResultDto calculate(CustomsCalculationRequestDto request) {
        PricingRatesDto rates = pricingConfigService.resolveEffectiveRates(request.getUserId());

        double oceanFreight = request.getOceanFreightCostUsd() != null ? request.getOceanFreightCostUsd() : rates.getOceanFreightRate();
        double usLandTransport = request.getUsLandTransportCostUsd() != null ? request.getUsLandTransportCostUsd() : rates.getUsLandTransportRate();
        double destinationHandling = request.getDestinationHandlingCostUsd() != null ? request.getDestinationHandlingCostUsd() : rates.getDestinationHandlingFee();
        double brokerFee = rates.getBrokerAndTerminalFee();
        double certFee = rates.getCertificationFee();
        double eurRate = request.getEurToUsdRate() != null ? request.getEurToUsdRate() : 1.08;

        double customsValueUsd = request.getAuctionPriceUsd() + usLandTransport + oceanFreight;

        if (request.getFuelType() == FuelType.ELECTRIC) {
            return calculateElectricVehicle(request, customsValueUsd, destinationHandling, brokerFee, certFee, eurRate);
        }

        return calculateInternalCombustion(request, customsValueUsd, destinationHandling, brokerFee, certFee, eurRate);
    }

    private CustomsCalculationResultDto calculateElectricVehicle(
            CustomsCalculationRequestDto req,
            double customsValueUsd,
            double destinationHandling,
            double brokerFee,
            double certFee,
            double eurRate) {

        double batteryKwh = req.getBatteryCapacityKwh() != null ? req.getBatteryCapacityKwh() : 75.0;
        double exciseEur = batteryKwh * 1.0;
        double exciseUsd = exciseEur * eurRate;
        double dutyUsd = 0.0;
        double vatUsd = 0.0;
        double pensionFundUsd = 0.0;

        double totalClearance = dutyUsd + exciseUsd + vatUsd + pensionFundUsd + brokerFee + certFee;
        double totalLandedBeforeRepair = customsValueUsd + destinationHandling + totalClearance;

        return CustomsCalculationResultDto.builder()
                .customsValueUsd(round(customsValueUsd))
                .importDutyUsd(0.0)
                .exciseTaxEur(round(exciseEur))
                .exciseTaxUsd(round(exciseUsd))
                .vatUsd(0.0)
                .pensionFundFeeUsd(0.0)
                .brokerAndTerminalFeeUsd(round(brokerFee))
                .certificationFeeUsd(round(certFee))
                .totalCustomsAndClearanceCostUsd(round(totalClearance))
                .totalLandedBeforeRepairUsd(round(totalLandedBeforeRepair))
                .legalBasisFormula("Law of Ukraine No. 2611-VIII: EV Excise 1 EUR/kWh, 0% Duty, 0% VAT")
                .isElectricBenefitApplied(true)
                .build();
    }

    private CustomsCalculationResultDto calculateInternalCombustion(
            CustomsCalculationRequestDto req,
            double customsValueUsd,
            double destinationHandling,
            double brokerFee,
            double certFee,
            double eurRate) {

        int currentYear = Year.now().getValue();
        int age = Math.min(15, Math.max(1, currentYear - req.getVehicleYear() - 1));
        int cc = req.getEngineCapacityCc() != null ? req.getEngineCapacityCc() : 2000;
        double displacementLiters = cc / 1000.0;

        double baseRateEur = resolveBaseExciseRateEur(req.getFuelType(), cc);
        double exciseEur = baseRateEur * displacementLiters * age;
        double exciseUsd = exciseEur * eurRate;

        double dutyUsd = customsValueUsd * 0.10;
        double vatUsd = (customsValueUsd + dutyUsd + exciseUsd) * 0.20;
        double pensionFundUsd = customsValueUsd * 0.04;

        double totalClearance = dutyUsd + exciseUsd + vatUsd + pensionFundUsd + brokerFee + certFee;
        double totalLandedBeforeRepair = customsValueUsd + destinationHandling + totalClearance;

        return CustomsCalculationResultDto.builder()
                .customsValueUsd(round(customsValueUsd))
                .importDutyUsd(round(dutyUsd))
                .exciseTaxEur(round(exciseEur))
                .exciseTaxUsd(round(exciseUsd))
                .vatUsd(round(vatUsd))
                .pensionFundFeeUsd(round(pensionFundUsd))
                .brokerAndTerminalFeeUsd(round(brokerFee))
                .certificationFeeUsd(round(certFee))
                .totalCustomsAndClearanceCostUsd(round(totalClearance))
                .totalLandedBeforeRepairUsd(round(totalLandedBeforeRepair))
                .legalBasisFormula(String.format("Base EUR %.0f x %.2f L x %d yrs + 10%% Duty + 20%% VAT", baseRateEur, displacementLiters, age))
                .isElectricBenefitApplied(false)
                .build();
    }

    private double resolveBaseExciseRateEur(FuelType fuelType, int cc) {
        if (fuelType == FuelType.DIESEL) {
            return cc > 3500 ? 150.0 : 75.0;
        }
        if (fuelType == FuelType.HYBRID || fuelType == FuelType.PLUGIN_HYBRID) {
            return 100.0;
        }
        return cc > 3000 ? 100.0 : 50.0;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
