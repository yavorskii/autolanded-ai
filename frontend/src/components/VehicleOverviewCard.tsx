import { VehicleSpecification } from '../types'
import { Zap, Cpu, Calendar, Navigation } from 'lucide-react'

interface Props {
  spec: VehicleSpecification
  auctionPrice: number
}

export const VehicleOverviewCard = ({ spec, auctionPrice }: Props) => {
  return (
    <div className="glass-card" style={{ padding: '24px' }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '16px' }}>
        <div>
          <span className="badge badge-info" style={{ marginBottom: '6px' }}>{spec.make} • {spec.year}</span>
          <h2 style={{ fontSize: '22px', fontWeight: 700 }}>{spec.make} {spec.model}</h2>
          <p className="mono" style={{ fontSize: '13px', color: 'var(--text-muted)' }}>VIN: {spec.vin}</p>
        </div>
        <div style={{ textAlign: 'right' }}>
          <div style={{ fontSize: '12px', color: 'var(--text-muted)' }}>Виграна ставка Copart</div>
          <div className="mono" style={{ fontSize: '26px', fontWeight: 800, color: 'var(--accent-cyan)' }}>
            ${auctionPrice.toLocaleString()}
          </div>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '12px', marginTop: '16px' }}>
        <div style={{ background: 'rgba(255, 255, 255, 0.03)', padding: '12px', borderRadius: '10px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '6px', color: 'var(--text-muted)', fontSize: '12px' }}>
            <Zap size={14} color="#00d2ff" />
            <span>Тип приводу/палива</span>
          </div>
          <div style={{ fontWeight: 600, fontSize: '14px', marginTop: '4px' }}>
            {spec.fuelType === 'ELECTRIC' ? '100% Electric (EV)' : spec.fuelType}
          </div>
        </div>

        <div style={{ background: 'rgba(255, 255, 255, 0.03)', padding: '12px', borderRadius: '10px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '6px', color: 'var(--text-muted)', fontSize: '12px' }}>
            <Cpu size={14} color="#10b981" />
            <span>Батарея / Двигун</span>
          </div>
          <div style={{ fontWeight: 600, fontSize: '14px', marginTop: '4px' }}>
            {spec.batteryCapacityKwh > 0 ? `${spec.batteryCapacityKwh} кВт·год` : `${spec.engineCapacityCc} см³`}
          </div>
        </div>

        <div style={{ background: 'rgba(255, 255, 255, 0.03)', padding: '12px', borderRadius: '10px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '6px', color: 'var(--text-muted)', fontSize: '12px' }}>
            <Navigation size={14} color="#f59e0b" />
            <span>Привід коліс</span>
          </div>
          <div style={{ fontWeight: 600, fontSize: '14px', marginTop: '4px' }}>{spec.driveType}</div>
        </div>

        <div style={{ background: 'rgba(255, 255, 255, 0.03)', padding: '12px', borderRadius: '10px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '6px', color: 'var(--text-muted)', fontSize: '12px' }}>
            <Calendar size={14} color="#a855f7" />
            <span>Кузов / Клас</span>
          </div>
          <div style={{ fontWeight: 600, fontSize: '14px', marginTop: '4px' }}>{spec.vehicleType}</div>
        </div>
      </div>
    </div>
  )
}
