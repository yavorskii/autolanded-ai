package ua.pnu.autolanded.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.pnu.autolanded.dto.*;
import ua.pnu.autolanded.entity.CarPart;
import ua.pnu.autolanded.entity.DamageZone;
import ua.pnu.autolanded.repository.CarPartRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PartCatalogService {

    private final CarPartRepository carPartRepository;
    private final PricingConfigService pricingConfigService;
    private final EpcResolverService epcResolverService;

    @PostConstruct
    public void initSeedData() {
        if (carPartRepository.count() == 0) {
            seedTeslaModelXParts();
            seedBmwI3Parts();
        }
    }

    @Transactional(readOnly = true)
    public RepairEstimateResultDto estimateRepair(RepairEstimateRequestDto request) {
        PricingRatesDto rates = pricingConfigService.resolveEffectiveRates(request.getUserId());

        double paintRate = rates.getPaintPricePerElement();
        double laborHourlyRate = rates.getLaborRatePerHour();

        List<CarPartInspectionResultDto> items = new ArrayList<>();

        double sumOptimistic = 0.0;
        double sumRealistic = 0.0;
        double sumPessimistic = 0.0;
        double totalParts = 0.0;
        double totalLabor = 0.0;
        double totalPaint = 0.0;

        for (DamageZone zone : request.getDamagedZones()) {
            CarPart part = carPartRepository.findFirstByMakeIgnoreCaseAndModelIgnoreCaseAndDamageZone(
                    request.getMake(), request.getModel(), zone
            ).orElseGet(() -> buildGenericFallbackPart(request.getMake(), request.getModel(), zone));

            List<PartStrategyOptionDto> strategies = buildStrategies(part, paintRate, laborHourlyRate);
            PartStrategyOptionDto realisticChoice = strategies.get(1);

            double minOptionCost = strategies.stream()
                    .mapToDouble(PartStrategyOptionDto::getTotalItemCostUsd)
                    .min().orElse(realisticChoice.getTotalItemCostUsd());

            double maxOptionCost = strategies.stream()
                    .mapToDouble(PartStrategyOptionDto::getTotalItemCostUsd)
                    .max().orElse(realisticChoice.getTotalItemCostUsd());

            totalParts += realisticChoice.getPartPriceUsd();
            totalLabor += realisticChoice.getLaborCostUsd();
            totalPaint += realisticChoice.getPaintCostUsd();

            sumOptimistic += minOptionCost;
            sumRealistic += realisticChoice.getTotalItemCostUsd();
            sumPessimistic += maxOptionCost;

            items.add(CarPartInspectionResultDto.builder()
                    .damageZone(zone)
                    .partNameUa(part.getPartNameUa())
                    .oemPartNumber(part.getOemPartNumber())
                    .diagramPosition(part.getDiagramPosition())
                    .diagramImageUrl(part.getDiagramImageUrl())
                    .avtoProSearchUrl(epcResolverService.buildAvtoProSearchUrl(part.getOemPartNumber(), part.getPartNameUa()))
                    .allegroSearchUrl(epcResolverService.buildAllegroSearchUrl(part.getOemPartNumber(), part.getPartNameEn()))
                    .strategyOptions(strategies)
                    .selectedStrategyCostUsd(realisticChoice.getTotalItemCostUsd())
                    .build());
        }

        if (request.isAirbagsDeployed()) {
            double airbagPrice = rates.getAirbagRestorePrice() * 2.0;
            sumOptimistic += airbagPrice * 0.8;
            sumRealistic += airbagPrice;
            sumPessimistic += airbagPrice * 1.3;
            totalParts += airbagPrice;
        }

        double riskBuffer = sumRealistic * 0.15;
        double realisticWithBuffer = sumRealistic + riskBuffer;
        double pessimisticWithBuffer = sumPessimistic * 1.30;

        return RepairEstimateResultDto.builder()
                .make(request.getMake())
                .model(request.getModel())
                .totalDamagedZonesCount(request.getDamagedZones().size())
                .optimisticTotalRepairCostUsd(round(sumOptimistic))
                .realisticTotalRepairCostUsd(round(realisticWithBuffer))
                .pessimisticTotalRepairCostUsd(round(pessimisticWithBuffer))
                .totalPartsCostUsd(round(totalParts))
                .totalLaborCostUsd(round(totalLabor))
                .totalPaintCostUsd(round(totalPaint))
                .riskBufferCostUsd(round(riskBuffer))
                .items(items)
                .build();
    }

    private List<PartStrategyOptionDto> buildStrategies(CarPart part, double paintRate, double laborHourlyRate) {
        List<PartStrategyOptionDto> list = new ArrayList<>();
        double laborCost = part.getLaborHoursToReplace() * laborHourlyRate;
        double paintCost = part.isRequiresPaint() ? paintRate : 0.0;

        double inColorPartPrice = part.getUsedOriginalInColorPriceUsd() != null ? part.getUsedOriginalInColorPriceUsd() : 450.0;
        list.add(PartStrategyOptionDto.builder()
                .strategyCode("IN_COLOR_OEM")
                .titleUa("Оригінал б/в у колір авто (без фарбування)")
                .partPriceUsd(inColorPartPrice)
                .paintCostUsd(0.0)
                .laborCostUsd(round(laborCost))
                .totalItemCostUsd(round(inColorPartPrice + laborCost))
                .isPaintIncluded(false)
                .isRecommended(false)
                .build());

        double taiwanPartPrice = part.getTaiwanNewPriceUsd() != null ? part.getTaiwanNewPriceUsd() : 350.0;
        list.add(PartStrategyOptionDto.builder()
                .strategyCode("TAIWAN_NEW_PAINTED")
                .titleUa("Новий Тайвань / ліцензія + фарбування")
                .partPriceUsd(taiwanPartPrice)
                .paintCostUsd(round(paintCost))
                .laborCostUsd(round(laborCost))
                .totalItemCostUsd(round(taiwanPartPrice + paintCost + laborCost))
                .isPaintIncluded(part.isRequiresPaint())
                .isRecommended(true)
                .build());

        double usedToPaintPartPrice = part.getUsedOriginalToPaintPriceUsd() != null ? part.getUsedOriginalToPaintPriceUsd() : 400.0;
        list.add(PartStrategyOptionDto.builder()
                .strategyCode("USED_OEM_PAINTED")
                .titleUa("Оригінал б/в з розборки + фарбування")
                .partPriceUsd(usedToPaintPartPrice)
                .paintCostUsd(round(paintCost))
                .laborCostUsd(round(laborCost))
                .totalItemCostUsd(round(usedToPaintPartPrice + paintCost + laborCost))
                .isPaintIncluded(part.isRequiresPaint())
                .isRecommended(false)
                .build());

        return list;
    }

    private CarPart buildGenericFallbackPart(String make, String model, DamageZone zone) {
        return CarPart.builder()
                .make(make)
                .model(model)
                .damageZone(zone)
                .partNameUa(zone.name().replace('_', ' '))
                .partNameEn(zone.name())
                .oemPartNumber("GEN-" + zone.name())
                .diagramPosition(1)
                .diagramImageUrl("/diagrams/generic_exploded.svg")
                .taiwanNewPriceUsd(300.0)
                .usedOriginalInColorPriceUsd(450.0)
                .usedOriginalToPaintPriceUsd(350.0)
                .laborHoursToReplace(2.0)
                .requiresPaint(true)
                .build();
    }

    private void seedTeslaModelXParts() {
        carPartRepository.save(CarPart.builder()
                .make("Tesla").model("Model X").yearFrom(2016).yearTo(2021)
                .damageZone(DamageZone.FRONT_BUMPER)
                .partNameUa("Передній бампер у зборі")
                .partNameEn("Front Bumper Fascia Assembly")
                .oemPartNumber("1083983-00-A")
                .diagramPosition(1)
                .diagramImageUrl("/diagrams/tesla_model_x_front_bumper.svg")
                .taiwanNewPriceUsd(450.0)
                .usedOriginalInColorPriceUsd(650.0)
                .usedOriginalToPaintPriceUsd(400.0)
                .laborHoursToReplace(2.5)
                .requiresPaint(true)
                .build());

        carPartRepository.save(CarPart.builder()
                .make("Tesla").model("Model X").yearFrom(2016).yearTo(2021)
                .damageZone(DamageZone.RIGHT_HEADLIGHT)
                .partNameUa("Фара передня права Full LED")
                .partNameEn("Headlamp Assembly Right Hand LED")
                .oemPartNumber("1034389-00-C")
                .diagramPosition(3)
                .diagramImageUrl("/diagrams/tesla_model_x_lighting.svg")
                .taiwanNewPriceUsd(650.0)
                .usedOriginalInColorPriceUsd(850.0)
                .usedOriginalToPaintPriceUsd(800.0)
                .laborHoursToReplace(1.0)
                .requiresPaint(false)
                .build());

        carPartRepository.save(CarPart.builder()
                .make("Tesla").model("Model X").yearFrom(2016).yearTo(2021)
                .damageZone(DamageZone.RIGHT_FENDER)
                .partNameUa("Крило переднє праве (алюміній)")
                .partNameEn("Front Fender Right Hand Aluminum")
                .oemPartNumber("1034360-00-D")
                .diagramPosition(7)
                .diagramImageUrl("/diagrams/tesla_model_x_fender.svg")
                .taiwanNewPriceUsd(280.0)
                .usedOriginalInColorPriceUsd(420.0)
                .usedOriginalToPaintPriceUsd(300.0)
                .laborHoursToReplace(2.0)
                .requiresPaint(true)
                .build());

        carPartRepository.save(CarPart.builder()
                .make("Tesla").model("Model X").yearFrom(2016).yearTo(2021)
                .damageZone(DamageZone.HOOD)
                .partNameUa("Капот передній (алюміній)")
                .partNameEn("Front Hood Panel Aluminum")
                .oemPartNumber("1034346-00-B")
                .diagramPosition(10)
                .diagramImageUrl("/diagrams/tesla_model_x_hood.svg")
                .taiwanNewPriceUsd(450.0)
                .usedOriginalInColorPriceUsd(680.0)
                .usedOriginalToPaintPriceUsd(480.0)
                .laborHoursToReplace(2.0)
                .requiresPaint(true)
                .build());

        carPartRepository.save(CarPart.builder()
                .make("Tesla").model("Model X").yearFrom(2016).yearTo(2021)
                .damageZone(DamageZone.COOLING_PUMP)
                .partNameUa("Помпа охолодження батареї та інвертора")
                .partNameEn("Battery Coolant Pump Assembly")
                .oemPartNumber("1058097-00-B")
                .diagramPosition(18)
                .diagramImageUrl("/diagrams/tesla_model_x_cooling.svg")
                .taiwanNewPriceUsd(180.0)
                .usedOriginalInColorPriceUsd(240.0)
                .usedOriginalToPaintPriceUsd(220.0)
                .laborHoursToReplace(2.0)
                .requiresPaint(false)
                .build());
    }

    private void seedBmwI3Parts() {
        carPartRepository.save(CarPart.builder()
                .make("BMW").model("i3").yearFrom(2014).yearTo(2021)
                .damageZone(DamageZone.COOLING_PUMP)
                .partNameUa("Вакуумний насос гальмівної системи / помпа")
                .partNameEn("Electric Vacuum Pump Unit")
                .oemPartNumber("34336857405")
                .diagramPosition(14)
                .diagramImageUrl("/diagrams/bmw_i3_vacuum_pump_diagram.svg")
                .taiwanNewPriceUsd(160.0)
                .usedOriginalInColorPriceUsd(220.0)
                .usedOriginalToPaintPriceUsd(200.0)
                .laborHoursToReplace(1.5)
                .requiresPaint(false)
                .build());
    }

    private double round(double val) {
        return Math.round(val * 100.0) / 100.0;
    }
}
