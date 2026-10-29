import React, { useState } from 'react'
import { Search, Sparkles } from 'lucide-react'

interface HeroSearchProps {
  onSearch: (vin: string, price: number, isFlood: boolean) => void
  onRunDemoTesla: () => void
  loading: boolean
}

export const HeroSearch = ({ onSearch, onRunDemoTesla, loading }: HeroSearchProps) => {
  const [vin, setVin] = useState('5YJXCDE23LF272040')
  const [price, setPrice] = useState('7050')
  const [isFlood, setIsFlood] = useState(false)
  const [activeTab, setActiveTab] = useState<'vin' | 'url' | 'manual'>('vin')

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    if (!vin.trim()) return
    const parsedPrice = parseFloat(price) || 7050
    onSearch(vin.trim(), parsedPrice, isFlood)
  }

  return (
    <div className="glass-card" style={{
      maxWidth: '1000px',
      margin: '40px auto 30px auto',
      padding: '32px',
      background: 'linear-gradient(180deg, rgba(17, 24, 39, 0.8) 0%, rgba(13, 18, 29, 0.95) 100%)',
      border: '1px solid rgba(0, 210, 255, 0.2)',
      boxShadow: '0 20px 50px rgba(0, 0, 0, 0.5)'
    }}>
      <div style={{ display: 'flex', gap: '8px', marginBottom: '24px', borderBottom: '1px solid var(--border)', paddingBottom: '16px' }}>
        <button
          type="button"
          onClick={() => setActiveTab('vin')}
          style={{
            background: activeTab === 'vin' ? 'rgba(0, 210, 255, 0.15)' : 'transparent',
            color: activeTab === 'vin' ? 'var(--accent-cyan)' : 'var(--text-secondary)',
            border: activeTab === 'vin' ? '1px solid var(--accent-cyan)' : '1px solid transparent',
            padding: '8px 16px',
            borderRadius: '8px',
            fontWeight: 600,
            cursor: 'pointer',
            fontSize: '14px'
          }}
        >
          За VIN-кодом (Copart / IAAI)
        </button>
        <button
          type="button"
          onClick={() => setActiveTab('url')}
          style={{
            background: activeTab === 'url' ? 'rgba(0, 210, 255, 0.15)' : 'transparent',
            color: activeTab === 'url' ? 'var(--accent-cyan)' : 'var(--text-secondary)',
            border: activeTab === 'url' ? '1px solid var(--accent-cyan)' : '1px solid transparent',
            padding: '8px 16px',
            borderRadius: '8px',
            fontWeight: 600,
            cursor: 'pointer',
            fontSize: '14px'
          }}
        >
          За посиланням на лот (PLC / BidFax)
        </button>
        <button
          type="button"
          onClick={() => setActiveTab('manual')}
          style={{
            background: activeTab === 'manual' ? 'rgba(0, 210, 255, 0.15)' : 'transparent',
            color: activeTab === 'manual' ? 'var(--accent-cyan)' : 'var(--text-secondary)',
            border: activeTab === 'manual' ? '1px solid var(--accent-cyan)' : '1px solid transparent',
            padding: '8px 16px',
            borderRadius: '8px',
            fontWeight: 600,
            cursor: 'pointer',
            fontSize: '14px'
          }}
        >
          Ручне завантаження фото
        </button>
      </div>

      <form onSubmit={handleSubmit}>
        <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr auto', gap: '16px', alignItems: 'center' }}>
          <div>
            <label style={{ display: 'block', fontSize: '13px', fontWeight: 600, color: 'var(--text-secondary)', marginBottom: '8px' }}>
              17-значний VIN-код автомобіля
            </label>
            <input
              type="text"
              className="input-custom mono"
              value={vin}
              onChange={(e) => setVin(e.target.value.toUpperCase())}
              placeholder="5YJXCDE23LF272040"
              maxLength={17}
              required
              style={{ width: '100%', textTransform: 'uppercase', letterSpacing: '1px', fontWeight: 600 }}
            />
          </div>

          <div>
            <label style={{ display: 'block', fontSize: '13px', fontWeight: 600, color: 'var(--text-secondary)', marginBottom: '8px' }}>
              Ставка аукціону ($ USD)
            </label>
            <input
              type="number"
              className="input-custom mono"
              value={price}
              onChange={(e) => setPrice(e.target.value)}
              placeholder="7050"
              required
              style={{ width: '100%', fontWeight: 600 }}
            />
          </div>

          <div style={{ alignSelf: 'flex-end' }}>
            <button
              type="submit"
              className="btn-primary"
              disabled={loading}
              style={{ height: '50px', padding: '0 28px', opacity: loading ? 0.7 : 1 }}
            >
              {loading ? (
                <>
                  <div className="pulse-circle" />
                  <span>ШІ Аналізує...</span>
                </>
              ) : (
                <>
                  <Search size={18} />
                  <span>Провести аудит</span>
                </>
              )}
            </button>
          </div>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginTop: '18px' }}>
          <label style={{ display: 'flex', alignItems: 'center', gap: '8px', cursor: 'pointer', fontSize: '13px', color: 'var(--text-muted)' }}>
            <input
              type="checkbox"
              checked={isFlood}
              onChange={(e) => setIsFlood(e.target.checked)}
              style={{ accentColor: 'var(--accent-rose)', width: '16px', height: '16px' }}
            />
            <span>Позначити як Flood (утопленик / перевірка батареї)</span>
          </label>

          <button
            type="button"
            onClick={onRunDemoTesla}
            style={{
              background: 'rgba(0, 210, 255, 0.08)',
              color: 'var(--accent-cyan)',
              border: '1px solid rgba(0, 210, 255, 0.3)',
              padding: '6px 14px',
              borderRadius: '8px',
              fontSize: '12px',
              fontWeight: 600,
              cursor: 'pointer',
              display: 'flex',
              alignItems: 'center',
              gap: '6px'
            }}
          >
            <Sparkles size={14} />
            <span>Демо: Tesla Model X 2020 ($7,050 Copart)</span>
          </button>
        </div>
      </form>
    </div>
  )
}
