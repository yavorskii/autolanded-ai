export type FuelType = 'PETROL' | 'DIESEL' | 'ELECTRIC' | 'HYBRID' | 'PLUGIN_HYBRID'
export type DamageSeverity = 'MINOR' | 'MODERATE' | 'SEVERE' | 'TOTAL_LOSS'
export type AuditVerdict = 'STRONG_BUY' | 'MODERATE_BUY' | 'CONSIDER' | 'RISKY' | 'TOTAL_LOSS'

export interface VehicleSpecification {
  vin: string
  make: string
  model: string
  year: number
  fuelType: FuelType
  engineCapacityCc: number
  batteryCapacityKwh: number
  driveType: string
  transmission: string
  vehicleType: string
}

export interface AiDamageFinding {
  damageZone: string
  severity: DamageSeverity
  confidenceScore: number
  suggestedAction: string
  observationsUa: string
}

export interface AiInspectionResult {
  overallSeverity: DamageSeverity
  airbagsDeployed: boolean
  isTotalLossRisk: boolean
  detectedZones: string[]
  detailedFindings: AiDamageFinding[]
  modelSpecificRisks: string[]
  inspectionSummaryUa: string
  isSimulatedOfflineFallback: boolean
}

export interface CustomsCalculationResult {
  customsValueUsd: number
  importDutyUsd: number
  exciseTaxEur: number
  exciseTaxUsd: number
  vatUsd: number
  pensionFundFeeUsd: number
  brokerAndTerminalFeeUsd: number
  certificationFeeUsd: number
  totalCustomsAndClearanceCostUsd: number
  totalLandedBeforeRepairUsd: number
  legalBasisFormula: string
  isElectricBenefitApplied: boolean
}

export interface PartStrategyOption {
  strategyCode: string
  titleUa: string
  partPriceUsd: number
  paintCostUsd: number
  laborCostUsd: number
  totalItemCostUsd: number
  isPaintIncluded: boolean
  isRecommended: boolean
}

export interface CarPartInspectionResult {
  damageZone: string
  partNameUa: string
  oemPartNumber: string
  diagramPosition: number
  diagramImageUrl: string
  avtoProSearchUrl: string
  allegroSearchUrl: string
  strategyOptions: PartStrategyOption[]
  selectedStrategyCostUsd: number
}

export interface RepairEstimateResult {
  make: string
  model: string
  totalDamagedZonesCount: number
  optimisticTotalRepairCostUsd: number
  realisticTotalRepairCostUsd: number
  pessimisticTotalRepairCostUsd: number
  totalPartsCostUsd: number
  totalLaborCostUsd: number
  totalPaintCostUsd: number
  riskBufferCostUsd: number
  items: CarPartInspectionResult[]
}

export interface MarketValuation {
  make: string
  model: string
  yearFrom: number
  yearTo: number
  bottomMarketPriceUsd: number
  medianMarketPriceUsd: number
  topMarketPriceUsd: number
  activeListingsCount: number
  autoRiaQueryUrl: string
  analyticalNoteUa: string
}

export interface FullVehicleAuditReport {
  id?: number
  vin: string
  specification: VehicleSpecification
  aiInspection: AiInspectionResult
  customs: CustomsCalculationResult
  repairEstimate: RepairEstimateResult
  marketValuation: MarketValuation
  totalLandedTurnkeyUsd: number
  netProfitUsd: number
  roiPercentage: number
  verdict: AuditVerdict
  executiveSummaryUa: string
  createdAt?: string
}

export interface PricingRates {
  paintPricePerElement: number
  laborRatePerHour: number
  airbagRestorePrice: number
  geometryBenchHourlyRate: number
  brokerAndTerminalFee: number
  certificationFee: number
  oceanFreightRate: number
  usLandTransportRate: number
  destinationHandlingFee: number
  isCustomUserConfig: boolean
}
