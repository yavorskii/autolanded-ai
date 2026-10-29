import { useState } from 'react'
import { RepairEstimateResult } from '../types'
import { Layers, ExternalLink, Search } from 'lucide-react'

interface Props {
  estimate: RepairEstimateResult
  vin: string
}

export const EpcPartsExplodedCard = ({ estimate, vin }: Props) => {
  const [selectedStrategies, setSelectedStrategies] = useState<{ [key: string]: string }>(() => {
    const initial: { [key: string]: string } = {}
    estimate.items.forEach(item => {
      initial[item.damageZone] = 'TAIWAN_NEW_PAINTED'
    })
    return initial
  })

  const getEpcCatalogUrl = () => {
    if (estimate.make.toLowerCase().includes('tesla')) {
      return 'https://epc.tesla.com/'
    }
    if (estimate.make.toLowerCase().includes('bmw')) {
      const last7 = vin.length >= 7 ? vin.substring(vin.length - 7) : ''
      return `https://www.realoem.com/bmw/enUS/select?vin=${last7}`
    }
    return `https://7zap.com/en/catalog/cars/${estimate.make}/`
  }

  const handleSelectStrategy = (zone: string, strategyCode: string) => {
    setSelectedStrategies(prev => ({ ...prev, [zone]: strategyCode }))
  }

  return (
    <div className="glass-card" style={{ padding: '24px' }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '20px', flexWrap: 'wrap', gap: '12px' }}>
        <div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <Layers size={20} color="var(--accent-cyan)" />
            <h3 style={{ fontSize: '18px', fontWeight: 700 }}>
              Каталог запчастин (OEM Парт-номери та Вибух-схеми EPC)
            </h3>
          </div>
          <p style={{ fontSize: '12px', color: 'var(--text-muted)', marginTop: '4px' }}>
            Стандарт каталогізації за зразком RealOEM / Tesla EPC / Алето Авто
          </p>
        </div>

        <a
          href={getEpcCatalogUrl()}
          target="_blank"
          rel="noopener noreferrer"
          className="btn-secondary"
          style={{ fontSize: '12px', padding: '6px 12px' }}
        >
          <span>Оригінальний каталог {estimate.make} EPC</span>
          <ExternalLink size={14} />
        </a>
      </div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
        {estimate.items.map((item) => {
          const currentStrategy = selectedStrategies[item.damageZone] || 'TAIWAN_NEW_PAINTED'
          const activeOption = item.strategyOptions.find(o => o.strategyCode === currentStrategy) || item.strategyOptions[1]

          return (
            <div
              key={item.damageZone}
              style={{
                background: 'rgba(255, 255, 255, 0.02)',
                border: '1px solid var(--border)',
                borderRadius: '14px',
                padding: '16px',
                display: 'grid',
                gridTemplateColumns: '1fr 1.6fr auto',
                gap: '20px',
                alignItems: 'center'
              }}
            >
              <div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '6px' }}>
                  <span style={{
                    background: 'rgba(0, 210, 255, 0.15)',
                    color: 'var(--accent-cyan)',
                    padding: '2px 8px',
                    borderRadius: '6px',
                    fontSize: '11px',
                    fontWeight: 700
                  }}>
                    #{item.diagramPosition || 1} на схемі
                  </span>
                  <h4 style={{ fontSize: '15px', fontWeight: 600 }}>{item.partNameUa}</h4>
                </div>

                <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginTop: '6px' }}>
                  <span className="mono" style={{ fontSize: '13px', color: '#93c5fd', background: 'rgba(59, 130, 246, 0.1)', padding: '2px 8px', borderRadius: '4px' }}>
                    OEM: {item.oemPartNumber}
                  </span>
                </div>

                <div style={{ display: 'flex', gap: '8px', marginTop: '10px' }}>
                  <a
                    href={item.avtoProSearchUrl}
                    target="_blank"
                    rel="noopener noreferrer"
                    style={{
                      fontSize: '11px',
                      color: 'var(--accent-cyan)',
                      textDecoration: 'none',
                      display: 'flex',
                      alignItems: 'center',
                      gap: '4px',
                      background: 'rgba(0, 210, 255, 0.08)',
                      padding: '4px 8px',
                      borderRadius: '6px'
                    }}
                  >
                    <Search size={12} />
                    <span>Знайти на Avto.pro</span>
                  </a>

                  <a
                    href={item.allegroSearchUrl}
                    target="_blank"
                    rel="noopener noreferrer"
                    style={{
                      fontSize: '11px',
                      color: '#f97316',
                      textDecoration: 'none',
                      display: 'flex',
                      alignItems: 'center',
                      gap: '4px',
                      background: 'rgba(249, 115, 22, 0.08)',
                      padding: '4px 8px',
                      borderRadius: '6px'
                    }}
                  >
                    <ExternalLink size={12} />
                    <span>Шукати на Allegro (PL)</span>
                  </a>
                </div>
              </div>

              <div>
                <div style={{ fontSize: '11px', color: 'var(--text-muted)', marginBottom: '6px', fontWeight: 600 }}>
                  Оберіть стратегію покупки деталі:
                </div>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
                  {item.strategyOptions.map(opt => {
                    const isSelected = opt.strategyCode === currentStrategy
                    return (
                      <div
                        key={opt.strategyCode}
                        onClick={() => handleSelectStrategy(item.damageZone, opt.strategyCode)}
                        style={{
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'space-between',
                          padding: '6px 12px',
                          borderRadius: '8px',
                          cursor: 'pointer',
                          background: isSelected ? 'rgba(0, 210, 255, 0.12)' : 'rgba(255, 255, 255, 0.02)',
                          border: isSelected ? '1px solid rgba(0, 210, 255, 0.4)' : '1px solid rgba(255, 255, 255, 0.05)',
                          fontSize: '12px',
                          transition: 'all 0.15s ease'
                        }}
                      >
                        <span style={{ fontWeight: isSelected ? 600 : 400, color: isSelected ? '#fff' : 'var(--text-secondary)' }}>
                          {opt.titleUa}
                        </span>
                        <span className="mono" style={{ fontWeight: 700, color: isSelected ? 'var(--accent-cyan)' : 'var(--text-muted)' }}>
                          ${opt.totalItemCostUsd}
                        </span>
                      </div>
                    )
                  })}
                </div>
              </div>

              <div style={{ textAlign: 'right', minWidth: '110px' }}>
                <div style={{ fontSize: '11px', color: 'var(--text-muted)' }}>Обрано з роботою:</div>
                <div className="mono" style={{ fontSize: '20px', fontWeight: 800, color: '#38bdf8' }}>
                  ${activeOption.totalItemCostUsd}
                </div>
                {activeOption.isPaintIncluded && (
                  <span style={{ fontSize: '10px', color: '#10b981', display: 'block', marginTop: '2px' }}>
                    + фарбування враховано
                  </span>
                )}
              </div>
            </div>
          )
        })}
      </div>
    </div>
  )
}
