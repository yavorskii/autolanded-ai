import { AiInspectionResult } from '../types'
import { Eye, ShieldCheck, AlertTriangle } from 'lucide-react'

interface Props {
  inspection: AiInspectionResult
}

export const AiInspectionCard = ({ inspection }: Props) => {
  return (
    <div className="glass-card" style={{ padding: '24px' }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '16px' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <Eye size={20} color="var(--accent-cyan)" />
          <h3 style={{ fontSize: '18px', fontWeight: 700 }}>Мультимодальний ШІ-аналіз фотографій</h3>
        </div>
        <span className={inspection.isTotalLossRisk ? 'badge badge-danger' : 'badge badge-strong-buy'}>
          {inspection.isTotalLossRisk ? 'КРИТИЧНИЙ РИЗИК' : `Ступінь: ${inspection.overallSeverity}`}
        </span>
      </div>

      <div style={{
        background: 'rgba(0, 210, 255, 0.05)',
        border: '1px solid rgba(0, 210, 255, 0.15)',
        padding: '14px 18px',
        borderRadius: '12px',
        marginBottom: '16px',
        fontSize: '14px',
        lineHeight: 1.5
      }}>
        {inspection.inspectionSummaryUa}
      </div>

      <div style={{ display: 'flex', gap: '16px', alignItems: 'center', marginBottom: '16px' }}>
        <div style={{
          display: 'flex',
          alignItems: 'center',
          gap: '8px',
          background: inspection.airbagsDeployed ? 'rgba(244, 63, 94, 0.1)' : 'rgba(16, 185, 129, 0.1)',
          border: inspection.airbagsDeployed ? '1px solid rgba(244, 63, 94, 0.3)' : '1px solid rgba(16, 185, 129, 0.3)',
          padding: '8px 14px',
          borderRadius: '8px',
          fontSize: '13px',
          fontWeight: 600,
          color: inspection.airbagsDeployed ? '#fb7185' : '#34d399'
        }}>
          {inspection.airbagsDeployed ? <AlertTriangle size={16} /> : <ShieldCheck size={16} />}
          <span>{inspection.airbagsDeployed ? 'Airbags: Спрацювали подушки' : 'Airbags: Салон і подушки цілі'}</span>
        </div>

        <div style={{ fontSize: '13px', color: 'var(--text-muted)' }}>
          Виявлено зон деформації: <strong style={{ color: 'var(--text-primary)' }}>{inspection.detectedZones.length}</strong>
        </div>
      </div>

      <div style={{ display: 'flex', flexWrap: 'wrap', gap: '8px', marginBottom: '16px' }}>
        {inspection.detectedZones.map(zone => (
          <span key={zone} style={{
            background: 'rgba(255, 255, 255, 0.05)',
            border: '1px solid var(--border)',
            padding: '4px 10px',
            borderRadius: '6px',
            fontSize: '12px',
            fontWeight: 500,
            display: 'flex',
            alignItems: 'center',
            gap: '6px'
          }}>
            <span style={{ width: '6px', height: '6px', borderRadius: '50%', background: '#f59e0b' }} />
            {zone.replace('_', ' ')}
          </span>
        ))}
      </div>

      {inspection.modelSpecificRisks && inspection.modelSpecificRisks.length > 0 && (
        <div style={{ marginTop: '12px' }}>
          <div style={{ fontSize: '12px', fontWeight: 600, color: 'var(--accent-amber)', marginBottom: '6px', display: 'flex', alignItems: 'center', gap: '6px' }}>
            <AlertTriangle size={14} />
            <span>Технічні особливості моделі (Domain Heuristics):</span>
          </div>
          <ul style={{ listStyle: 'none', paddingLeft: 0 }}>
            {inspection.modelSpecificRisks.map((risk, idx) => (
              <li key={idx} style={{
                fontSize: '13px',
                color: '#fde68a',
                background: 'rgba(245, 158, 11, 0.08)',
                padding: '8px 12px',
                borderRadius: '6px',
                marginBottom: '6px',
                borderLeft: '3px solid #f59e0b'
              }}>
                {risk}
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  )
}
