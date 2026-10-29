import { CustomsCalculationResult, RepairEstimateResult, MarketValuation, AuditVerdict } from '../types'
import { Award } from 'lucide-react'

interface Props {
  auctionPrice: number
  customs: CustomsCalculationResult
  repair: RepairEstimateResult
  market: MarketValuation
  totalLanded: number
  netProfit: number
  roi: number
  verdict: AuditVerdict
  summary: string
}

export const FinancialSummaryCard = ({
  auctionPrice,
  customs,
  repair,
  market,
  totalLanded,
  netProfit,
  roi,
  verdict,
  summary
}: Props) => {
  const getVerdictBadge = () => {
    switch (verdict) {
      case 'STRONG_BUY':
        return <span className="badge badge-strong-buy" style={{ fontSize: '14px', padding: '6px 14px' }}>🟢 РЕКОМЕНДОВАНО (STRONG BUY)</span>
      case 'MODERATE_BUY':
        return <span className="badge badge-strong-buy" style={{ fontSize: '14px', padding: '6px 14px' }}>🟢 ВИГІДНИЙ ЛОТ (BUY)</span>
      case 'CONSIDER':
        return <span className="badge badge-warning" style={{ fontSize: '14px', padding: '6px 14px' }}>🟡 ПОТРЕБУЄ УТОЧНЕННЯ (CONSIDER)</span>
      case 'TOTAL_LOSS':
        return <span className="badge badge-danger" style={{ fontSize: '14px', padding: '6px 14px' }}>🔴 TOTAL LOSS (НЕ КУПУВАТИ)</span>
      default:
        return <span className="badge badge-danger" style={{ fontSize: '14px', padding: '6px 14px' }}>🔴 РИЗИКОВИЙ ЛОТ</span>
    }
  }

  return (
    <div className="glass-card glow-cyan" style={{
      padding: '28px',
      background: 'linear-gradient(135deg, rgba(17, 24, 39, 0.9) 0%, rgba(13, 22, 38, 0.95) 100%)',
      border: '1px solid rgba(0, 210, 255, 0.3)'
    }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '24px', flexWrap: 'wrap', gap: '12px' }}>
        <div>
          <span className="badge badge-info" style={{ marginBottom: '6px' }}>Фінансовий розрахунок під ключ</span>
          <h3 style={{ fontSize: '22px', fontWeight: 800 }}>Інвестиційна оцінка та митне очищення</h3>
        </div>
        {getVerdictBadge()}
      </div>

      <div style={{
        background: 'rgba(255, 255, 255, 0.03)',
        border: '1px solid var(--border)',
        borderRadius: '14px',
        padding: '16px 20px',
        marginBottom: '24px',
        fontSize: '14px',
        lineHeight: 1.6
      }}>
        <div style={{ display: 'flex', alignItems: 'flex-start', gap: '10px' }}>
          <Award size={20} color="var(--accent-cyan)" style={{ flexShrink: 0, marginTop: '2px' }} />
          <div>{summary}</div>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1.2fr 1fr', gap: '24px' }}>
        <div>
          <h4 style={{ fontSize: '14px', fontWeight: 600, color: 'var(--text-secondary)', marginBottom: '14px' }}>
            Структура собівартості "на номерах в Україні":
          </h4>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '14px' }}>
              <span style={{ color: 'var(--text-muted)' }}>1. Ставка на аукціоні Copart:</span>
              <span className="mono" style={{ fontWeight: 600 }}>${auctionPrice.toLocaleString()}</span>
            </div>

            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '14px' }}>
              <span style={{ color: 'var(--text-muted)' }}>2. Доставка (суша США + океан + автовоз):</span>
              <span className="mono" style={{ fontWeight: 600 }}>$2,600</span>
            </div>

            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '14px' }}>
              <span style={{ color: 'var(--text-muted)' }}>
                3. Митниця (Закон № 2611-VIII):
                {customs.isElectricBenefitApplied && <span style={{ color: '#10b981', marginLeft: '6px' }}>[EV Пільга]</span>}
              </span>
              <span className="mono" style={{ fontWeight: 600, color: '#38bdf8' }}>${customs.exciseTaxUsd}</span>
            </div>

            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '14px' }}>
              <span style={{ color: 'var(--text-muted)' }}>4. Брокерські послуги та сертифікація:</span>
              <span className="mono" style={{ fontWeight: 600 }}>${customs.brokerAndTerminalFeeUsd + customs.certificationFeeUsd}</span>
            </div>

            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '14px' }}>
              <span style={{ color: 'var(--text-muted)' }}>5. Відновлювальний ремонт (запчастини + СТО):</span>
              <span className="mono" style={{ fontWeight: 600, color: '#f59e0b' }}>${repair.realisticTotalRepairCostUsd.toLocaleString()}</span>
            </div>

            <div style={{ borderTop: '1px solid var(--border)', paddingTop: '10px', display: 'flex', justifyContent: 'space-between', fontSize: '16px', fontWeight: 700 }}>
              <span>РАЗОМ БЮДЖЕТ "НА НОМЕРАХ":</span>
              <span className="mono" style={{ color: 'var(--accent-cyan)' }}>${totalLanded.toLocaleString()}</span>
            </div>
          </div>
        </div>

        <div style={{ background: 'rgba(0, 210, 255, 0.03)', border: '1px solid rgba(0, 210, 255, 0.15)', borderRadius: '14px', padding: '20px' }}>
          <h4 style={{ fontSize: '14px', fontWeight: 600, color: 'var(--accent-cyan)', marginBottom: '14px' }}>
            Аналітика ринку Auto.ria (Like-for-Like):
          </h4>

          <div style={{ marginBottom: '16px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '13px', marginBottom: '4px' }}>
              <span style={{ color: 'var(--text-muted)' }}>Низ ринку (P25 - швидкий продаж):</span>
              <span className="mono" style={{ fontWeight: 700, color: '#fff' }}>${market.bottomMarketPriceUsd.toLocaleString()}</span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '13px', marginBottom: '4px' }}>
              <span style={{ color: 'var(--text-muted)' }}>Медіана ринку України:</span>
              <span className="mono" style={{ fontWeight: 700, color: 'var(--text-secondary)' }}>${market.medianMarketPriceUsd.toLocaleString()}</span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '13px' }}>
              <span style={{ color: 'var(--text-muted)' }}>Верх ринку (P75):</span>
              <span className="mono" style={{ fontWeight: 700, color: 'var(--text-muted)' }}>${market.topMarketPriceUsd.toLocaleString()}</span>
            </div>
          </div>

          <div style={{ borderTop: '1px solid rgba(0, 210, 255, 0.2)', paddingTop: '14px', textAlign: 'center' }}>
            <div style={{ fontSize: '12px', color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.5px' }}>
              Прогнозована чиста економія / вигода:
            </div>
            <div className="mono" style={{ fontSize: '32px', fontWeight: 900, color: '#34d399', margin: '4px 0' }}>
              +${netProfit.toLocaleString()}
            </div>
            <div style={{ fontSize: '13px', color: '#10b981', fontWeight: 600 }}>
              ROI: +{roi}% прибутку до вкладеного капіталу
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
