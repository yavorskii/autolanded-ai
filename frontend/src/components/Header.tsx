import { Car, Settings, History } from 'lucide-react'

interface HeaderProps {
  onOpenSettings: () => void
  onOpenHistory: () => void
}

export const Header = ({ onOpenSettings, onOpenHistory }: HeaderProps) => {
  return (
    <header style={{
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      padding: '18px 32px',
      borderBottom: '1px solid var(--border)',
      background: 'rgba(7, 9, 14, 0.85)',
      backdropFilter: 'blur(12px)',
      position: 'sticky',
      top: 0,
      zIndex: 100
    }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
        <div style={{
          background: 'linear-gradient(135deg, #00d2ff, #3a7bd5)',
          padding: '10px',
          borderRadius: '12px',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          boxShadow: '0 0 20px rgba(0, 210, 255, 0.4)'
        }}>
          <Car size={24} color="#fff" />
        </div>
        <div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <h1 style={{ fontSize: '20px', fontWeight: 800, letterSpacing: '-0.5px' }}>
              AutoLanded <span style={{ color: 'var(--accent-cyan)' }}>AI</span>
            </h1>
            <span className="badge badge-info" style={{ fontSize: '10px', padding: '2px 6px' }}>v1.0 DSS</span>
          </div>
          <p style={{ fontSize: '12px', color: 'var(--text-muted)' }}>
            Система оцінки пошкоджень та фінансового аудиту імпорту авто
          </p>
        </div>
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
        <div style={{
          display: 'flex',
          alignItems: 'center',
          gap: '8px',
          padding: '6px 12px',
          borderRadius: '8px',
          background: 'rgba(16, 185, 129, 0.08)',
          border: '1px solid rgba(16, 185, 129, 0.2)',
          fontSize: '12px',
          color: '#34d399'
        }}>
          <div className="pulse-circle" />
          <span>Gemini Vision & Customs 2611-VIII</span>
        </div>

        <button onClick={onOpenSettings} className="btn-secondary" title="Налаштування регіональних тарифів СТО">
          <Settings size={16} />
          <span>Тарифи СТО</span>
        </button>

        <button onClick={onOpenHistory} className="btn-secondary" title="Історія розрахунків">
          <History size={16} />
          <span>Історія</span>
        </button>
      </div>
    </header>
  )
}
