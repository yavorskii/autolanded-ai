import { useState } from 'react'
import { PricingRates } from '../types'
import { X, Save, RotateCcw, MapPin } from 'lucide-react'

interface Props {
  isOpen: boolean
  onClose: () => void
  currentRates: PricingRates
  onSave: (rates: Partial<PricingRates>) => void
  onReset: () => void
}

export const RegionalSettingsModal = ({
  isOpen,
  onClose,
  currentRates,
  onSave,
  onReset
}: Props) => {
  if (!isOpen) return null

  const [paintPrice, setPaintPrice] = useState(currentRates.paintPricePerElement)
  const [laborRate, setLaborRate] = useState(currentRates.laborRatePerHour)
  const [airbagPrice, setAirbagPrice] = useState(currentRates.airbagRestorePrice)
  const [brokerFee, setBrokerFee] = useState(currentRates.brokerAndTerminalFee)

  const applyPreset = (preset: 'kyiv' | 'if' | 'garage') => {
    if (preset === 'kyiv') {
      setPaintPrice(160)
      setLaborRate(35)
      setAirbagPrice(550)
      setBrokerFee(400)
    } else if (preset === 'if') {
      setPaintPrice(110)
      setLaborRate(20)
      setAirbagPrice(400)
      setBrokerFee(350)
    } else {
      setPaintPrice(70)
      setLaborRate(12)
      setAirbagPrice(300)
      setBrokerFee(300)
    }
  }

  const handleSave = () => {
    onSave({
      paintPricePerElement: Number(paintPrice),
      laborRatePerHour: Number(laborRate),
      airbagRestorePrice: Number(airbagPrice),
      brokerAndTerminalFee: Number(brokerFee)
    })
    onClose()
  }

  return (
    <div style={{
      position: 'fixed',
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
      background: 'rgba(0, 0, 0, 0.75)',
      backdropFilter: 'blur(8px)',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      zIndex: 1000
    }}>
      <div className="glass-card" style={{
        width: '550px',
        maxWidth: '90vw',
        padding: '28px',
        background: '#0d121d',
        border: '1px solid rgba(0, 210, 255, 0.3)'
      }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <MapPin size={20} color="var(--accent-cyan)" />
            <h3 style={{ fontSize: '18px', fontWeight: 700 }}>Регіональні тарифи робіт СТО</h3>
          </div>
          <button onClick={onClose} style={{ background: 'none', border: 'none', color: 'var(--text-muted)', cursor: 'pointer' }}>
            <X size={20} />
          </button>
        </div>

        <p style={{ fontSize: '13px', color: 'var(--text-secondary)', marginBottom: '16px' }}>
          Оберіть швидкий регіональний пресет або встановіть власні ціни робіт для персонального перерахунку:
        </p>

        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '8px', marginBottom: '20px' }}>
          <button type="button" onClick={() => applyPreset('if')} className="btn-secondary" style={{ fontSize: '12px', justifyContent: 'center' }}>
            Івано-Франківськ ($110)
          </button>
          <button type="button" onClick={() => applyPreset('kyiv')} className="btn-secondary" style={{ fontSize: '12px', justifyContent: 'center' }}>
            Київ / Центр ($160)
          </button>
          <button type="button" onClick={() => applyPreset('garage')} className="btn-secondary" style={{ fontSize: '12px', justifyContent: 'center' }}>
            Власний гараж ($70)
          </button>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '14px', marginBottom: '24px' }}>
          <div>
            <label style={{ display: 'block', fontSize: '12px', color: 'var(--text-muted)', marginBottom: '4px' }}>
              Фарбування 1 кузовного елемента ($ USD)
            </label>
            <input
              type="number"
              className="input-custom"
              value={paintPrice}
              onChange={e => setPaintPrice(Number(e.target.value))}
              style={{ width: '100%' }}
            />
          </div>

          <div>
            <label style={{ display: 'block', fontSize: '12px', color: 'var(--text-muted)', marginBottom: '4px' }}>
              Ставка нормо-години слюсарних/стапельних робіт ($/год)
            </label>
            <input
              type="number"
              className="input-custom"
              value={laborRate}
              onChange={e => setLaborRate(Number(e.target.value))}
              style={{ width: '100%' }}
            />
          </div>

          <div>
            <label style={{ display: 'block', fontSize: '12px', color: 'var(--text-muted)', marginBottom: '4px' }}>
              Відновлення блоку подушки безпеки Airbag ($ USD)
            </label>
            <input
              type="number"
              className="input-custom"
              value={airbagPrice}
              onChange={e => setAirbagPrice(Number(e.target.value))}
              style={{ width: '100%' }}
            />
          </div>
        </div>

        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <button
            type="button"
            onClick={() => { onReset(); onClose(); }}
            className="btn-secondary"
            style={{ fontSize: '12px' }}
          >
            <RotateCcw size={14} />
            <span>Скинути до середніх</span>
          </button>

          <button
            type="button"
            onClick={handleSave}
            className="btn-primary"
            style={{ fontSize: '13px', padding: '10px 20px' }}
          >
            <Save size={16} />
            <span>Зберегти налаштування</span>
          </button>
        </div>
      </div>
    </div>
  )
}
