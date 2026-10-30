import { useState, useEffect } from 'react'
import { Header } from './components/Header'
import { HeroSearch } from './components/HeroSearch'
import { VehicleOverviewCard } from './components/VehicleOverviewCard'
import { AiInspectionCard } from './components/AiInspectionCard'
import { EpcPartsExplodedCard } from './components/EpcPartsExplodedCard'
import { FinancialSummaryCard } from './components/FinancialSummaryCard'
import { RegionalSettingsModal } from './components/RegionalSettingsModal'
import { FullVehicleAuditReport, PricingRates } from './types'
import { api } from './api'

export default function App() {
  const [report, setReport] = useState<FullVehicleAuditReport | null>(null)
  const [loading, setLoading] = useState(false)
  const [isSettingsOpen, setIsSettingsOpen] = useState(false)
  const [isHistoryOpen, setIsHistoryOpen] = useState(false)
  const [historyList, setHistoryList] = useState<any[]>([])
  const [pricing, setPricing] = useState<PricingRates>({
    paintPricePerElement: 120,
    laborRatePerHour: 25,
    airbagRestorePrice: 450,
    geometryBenchHourlyRate: 35,
    brokerAndTerminalFee: 350,
    certificationFee: 250,
    oceanFreightRate: 1200,
    usLandTransportRate: 450,
    destinationHandlingFee: 950,
    isCustomUserConfig: false
  })

  useEffect(() => {
    loadDemoReport()
    loadPricing()
  }, [])

  const loadDemoReport = async () => {
    setLoading(true)
    try {
      const data = await api.runDemoTeslaX()
      setReport(data)
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  const loadPricing = async () => {
    try {
      const rates = await api.getEffectivePricing()
      setPricing(rates)
    } catch (err) {
      console.error(err)
    }
  }

  const handleSearch = async (vin: string, price: number, isFlood: boolean) => {
    setLoading(true)
    try {
      const data = await api.runAudit(vin, price, isFlood)
      setReport(data)
    } catch (err) {
      alert('Помилка при проведенні аудиту лота. Перевірте зʼєднання.')
    } finally {
      setLoading(false)
    }
  }

  const handleSavePricing = async (updated: Partial<PricingRates>) => {
    try {
      const saved = await api.updatePricing(1, updated)
      setPricing(saved)
      if (report) {
        handleSearch(report.vin, report.customs.customsValueUsd - 1650, report.aiInspection.isTotalLossRisk)
      }
    } catch (err) {
      console.error(err)
    }
  }

  const handleResetPricing = async () => {
    try {
      const reset = await api.resetPricing(1)
      setPricing(reset)
      if (report) {
        handleSearch(report.vin, report.customs.customsValueUsd - 1650, report.aiInspection.isTotalLossRisk)
      }
    } catch (err) {
      console.error(err)
    }
  }

  const handleOpenHistory = async () => {
    try {
      const items = await api.getAuditHistory()
      setHistoryList(items)
      setIsHistoryOpen(true)
    } catch (err) {
      console.error(err)
    }
  }

  return (
    <div style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
      <Header
        onOpenSettings={() => setIsSettingsOpen(true)}
        onOpenHistory={handleOpenHistory}
      />

      <main style={{ flex: 1, padding: '0 24px 60px 24px' }}>
        <HeroSearch
          onSearch={handleSearch}
          onRunDemoTesla={loadDemoReport}
          loading={loading}
        />

        {report && (
          <div style={{ maxWidth: '1200px', margin: '0 auto', display: 'flex', flexDirection: 'column', gap: '28px' }}>
            <VehicleOverviewCard
              spec={report.specification}
              auctionPrice={report.customs.customsValueUsd - 1650 > 0 ? report.customs.customsValueUsd - 1650 : 7050}
            />

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '24px' }}>
              <AiInspectionCard inspection={report.aiInspection} make={report.specification.make} model={report.specification.model} />
              <FinancialSummaryCard
                auctionPrice={report.customs.customsValueUsd - 1650 > 0 ? report.customs.customsValueUsd - 1650 : 7050}
                customs={report.customs}
                repair={report.repairEstimate}
                market={report.marketValuation}
                totalLanded={report.totalLandedTurnkeyUsd}
                netProfit={report.netProfitUsd}
                roi={report.roiPercentage}
                verdict={report.verdict}
                summary={report.executiveSummaryUa}
              />
            </div>

            <EpcPartsExplodedCard
              estimate={report.repairEstimate}
              vin={report.vin}
            />
          </div>
        )}
      </main>

      <footer style={{
        textAlign: 'center',
        padding: '24px',
        borderTop: '1px solid var(--border)',
        color: 'var(--text-muted)',
        fontSize: '13px'
      }}>
        AutoLanded AI • Дипломний проєкт (Спеціальність 121 "Інженерія програмного забезпечення", ПНУ)
      </footer>

      <RegionalSettingsModal
        isOpen={isSettingsOpen}
        onClose={() => setIsSettingsOpen(false)}
        currentRates={pricing}
        onSave={handleSavePricing}
        onReset={handleResetPricing}
      />

      {isHistoryOpen && (
        <div style={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          background: 'rgba(0,0,0,0.7)',
          backdropFilter: 'blur(6px)',
          display: 'flex',
          justifyContent: 'flex-end',
          zIndex: 1000
        }}>
          <div style={{
            width: '450px',
            maxWidth: '90vw',
            background: '#0d121d',
            height: '100%',
            padding: '28px',
            borderLeft: '1px solid var(--border)',
            overflowY: 'auto'
          }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
              <h3 style={{ fontSize: '18px', fontWeight: 700 }}>Історія аудитів</h3>
              <button onClick={() => setIsHistoryOpen(false)} className="btn-secondary" style={{ padding: '4px 10px' }}>Закрити</button>
            </div>

            {historyList.length === 0 ? (
              <p style={{ color: 'var(--text-muted)', fontSize: '14px' }}>Ще немає збережених розрахунків.</p>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                {historyList.map((item, idx) => (
                  <div key={idx} style={{
                    background: 'rgba(255,255,255,0.03)',
                    border: '1px solid var(--border)',
                    borderRadius: '10px',
                    padding: '14px'
                  }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '4px' }}>
                      <strong style={{ fontSize: '14px' }}>{item.make} {item.model} ({item.year})</strong>
                      <span className="badge badge-strong-buy" style={{ fontSize: '10px' }}>{item.verdict}</span>
                    </div>
                    <div className="mono" style={{ fontSize: '12px', color: 'var(--text-muted)' }}>{item.vin}</div>
                    <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '8px', fontSize: '13px' }}>
                      <span style={{ color: 'var(--text-muted)' }}>Собівартість: ${item.totalLandedTurnkeyUsd}</span>
                      <span style={{ color: '#34d399', fontWeight: 700 }}>Прибуток: +${item.netProfitUsd}</span>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  )
}
