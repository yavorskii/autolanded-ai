import { useState } from 'react'
import { AiInspectionResult } from '../types'
import { Eye, ShieldCheck, AlertTriangle, Crosshair, ChevronRight } from 'lucide-react'

interface Props {
  inspection: AiInspectionResult
  make?: string
  model?: string
}

interface BoundingBox {
  id: string
  labelUa: string
  zone: string
  severity: 'MINOR' | 'MODERATE' | 'SEVERE' | 'ALERT'
  confidence: number
  top: string
  left: string
  width: string
  height: string
  descriptionUa: string
  oemNumber: string
}

export const AiInspectionCard = ({ inspection, make = 'Tesla', model = 'Model X' }: Props) => {
  const [activePhotoIndex, setActivePhotoIndex] = useState(0)
  const [showOverlays, setShowOverlays] = useState(true)
  const [selectedBoxId, setSelectedBoxId] = useState<string | null>(null)

  // Real Copart salvage photos of Tesla Model X
  const photoAngles = [
    {
      id: 'front-right',
      title: 'Ракурс 1: Передній удар 3/4 (Copart Yard)',
      badge: 'Головне пошкодження',
      url: '/lots/tesla-x/front-right.jpg',
      boxes: [
        {
          id: 'b1',
          labelUa: 'Деформація підсилювача бампера & Фара',
          zone: 'FRONT_BUMPER',
          severity: 'SEVERE',
          confidence: 96,
          top: '53%',
          left: '48%',
          width: '24%',
          height: '26%',
          descriptionUa: 'Зминання абсорбера, розрив кріплень LED-модуля фари. Потрібна заміна в зборі.',
          oemNumber: '1083983-00-A'
        },
        {
          id: 'b2',
          labelUa: 'Деформація переднього правого крила',
          zone: 'FRONT_RIGHT_FENDER',
          severity: 'SEVERE',
          confidence: 93,
          top: '47%',
          left: '40%',
          width: '13%',
          height: '22%',
          descriptionUa: 'Глибокий залом алюмінієвої панелі крила над колісною аркою. Заміна під фарбування.',
          oemNumber: '1054488-00-A'
        },
        {
          id: 'b3',
          labelUa: 'Залом кромки капота',
          zone: 'HOOD',
          severity: 'MODERATE',
          confidence: 89,
          top: '44%',
          left: '52%',
          width: '26%',
          height: '16%',
          descriptionUa: 'Деформація капота над зоною замка та фари. Потрібне рихтування або заміна.',
          oemNumber: '1054488-00-A'
        }
      ] as BoundingBox[]
    },
    {
      id: 'frunk-cooling',
      title: 'Ракурс 2: Підкапотний простір & Охолодження',
      badge: 'Силова частина & Батарея',
      url: '/lots/tesla-x/frunk-damage.jpg',
      boxes: [
        {
          id: 'b4',
          labelUa: 'Розрив корпусу фари & Кронштейна',
          zone: 'HEADLIGHT_BRACKET',
          severity: 'SEVERE',
          confidence: 97,
          top: '50%',
          left: '56%',
          width: '32%',
          height: '32%',
          descriptionUa: 'Повний розрив пластикового корпусу LED фари, деформація монтажного телевізора.',
          oemNumber: '1083983-00-A'
        },
        {
          id: 'b5',
          labelUa: 'Ризик розриву контуру охолодження батареї',
          zone: 'COOLING_LOOP',
          severity: 'ALERT',
          confidence: 91,
          top: '43%',
          left: '32%',
          width: '25%',
          height: '34%',
          descriptionUa: 'Відкриті патрубки та радіатор контуру терморегуляції високовольтної батареї.',
          oemNumber: '1077057-00-C'
        }
      ] as BoundingBox[]
    },
    {
      id: 'interior',
      title: 'Ракурс 3: Салон & Панель приладів',
      badge: 'Салон: Подушки цілі',
      url: '/lots/tesla-x/interior.jpg',
      boxes: [
        {
          id: 'b6',
          labelUa: 'Кермо та подушка водія (SRS Intact)',
          zone: 'STEERING_AIRBAG',
          severity: 'MINOR',
          confidence: 99,
          top: '32%',
          left: '28%',
          width: '23%',
          height: '40%',
          descriptionUa: 'Подушка безпеки водія не спрацьовувала, маточина та піропатрон цілі.',
          oemNumber: '1064249-00-B'
        },
        {
          id: 'b7',
          labelUa: 'Центральний дисплей MCU (17")',
          zone: 'CENTER_SCREEN',
          severity: 'MINOR',
          confidence: 95,
          top: '34%',
          left: '42%',
          width: '14%',
          height: '33%',
          descriptionUa: 'Екран мультимедіа та торпедо без механічних пошкоджень.',
          oemNumber: '1084333-00-B'
        }
      ] as BoundingBox[]
    }
  ]

  const currentPhoto = photoAngles[activePhotoIndex]
  const selectedBox = currentPhoto.boxes.find(b => b.id === selectedBoxId) || currentPhoto.boxes[0]

  return (
    <div className="glass-card" style={{ padding: '24px' }}>
      {/* Header bar */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '16px', flexWrap: 'wrap', gap: '12px' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
          <div style={{
            background: 'rgba(0, 210, 255, 0.15)',
            border: '1px solid var(--accent-cyan)',
            padding: '6px',
            borderRadius: '8px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center'
          }}>
            <Crosshair size={20} color="var(--accent-cyan)" />
          </div>
          <div>
            <h3 style={{ fontSize: '18px', fontWeight: 700 }}>
              Мультимодальний комп'ютерний зір (Computer Vision Damage Inspection)
            </h3>
            <p style={{ fontSize: '12px', color: 'var(--text-muted)' }}>
              Детекція пошкоджень кузова та силових елементів {make} {model} за фотографіями аукціону Copart
            </p>
          </div>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          <button
            type="button"
            onClick={() => setShowOverlays(!showOverlays)}
            style={{
              background: showOverlays ? 'rgba(0, 210, 255, 0.2)' : 'rgba(255, 255, 255, 0.05)',
              border: showOverlays ? '1px solid var(--accent-cyan)' : '1px solid var(--border)',
              color: showOverlays ? 'var(--accent-cyan)' : 'var(--text-muted)',
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
            <Eye size={14} />
            <span>{showOverlays ? 'Розмітка ШІ: Увімкнено' : 'Розмітка ШІ: Вимкнено'}</span>
          </button>

          <span className={inspection.isTotalLossRisk ? 'badge badge-danger' : 'badge badge-strong-buy'}>
            {inspection.isTotalLossRisk ? 'КРИТИЧНИЙ РИЗИК' : `Ступінь: ${inspection.overallSeverity}`}
          </span>
        </div>
      </div>

      {/* Main Interactive Image Stage with Bounding Boxes */}
      <div style={{
        position: 'relative',
        width: '100%',
        height: '440px',
        borderRadius: '16px',
        overflow: 'hidden',
        border: '1px solid rgba(0, 210, 255, 0.25)',
        background: '#090d16',
        marginBottom: '16px'
      }}>
        <img
          src={currentPhoto.url}
          alt={currentPhoto.title}
          style={{
            width: '100%',
            height: '100%',
            objectFit: 'cover',
            filter: 'brightness(0.95) contrast(1.02)'
          }}
        />

        {/* Scanline HUD Grid Overlay */}
        <div style={{
          position: 'absolute',
          inset: 0,
          backgroundImage: 'linear-gradient(rgba(0, 210, 255, 0.03) 1px, transparent 1px), linear-gradient(90deg, rgba(0, 210, 255, 0.03) 1px, transparent 1px)',
          backgroundSize: '40px 40px',
          pointerEvents: 'none'
        }} />

        {/* Top telemetry bar */}
        <div style={{
          position: 'absolute',
          top: '12px',
          left: '12px',
          right: '12px',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          pointerEvents: 'none'
        }}>
          <div style={{
            background: 'rgba(9, 13, 22, 0.85)',
            backdropFilter: 'blur(8px)',
            border: '1px solid rgba(0, 210, 255, 0.3)',
            padding: '6px 12px',
            borderRadius: '6px',
            fontSize: '11px',
            fontWeight: 700,
            color: 'var(--accent-cyan)',
            display: 'flex',
            alignItems: 'center',
            gap: '6px'
          }}>
            <span style={{ width: '8px', height: '8px', borderRadius: '50%', background: '#10b981', display: 'inline-block' }} />
            <span>AI SCAN ACTIVE // COPART LOT #34567891</span>
          </div>

          <div style={{
            background: 'rgba(9, 13, 22, 0.85)',
            backdropFilter: 'blur(8px)',
            border: '1px solid var(--border)',
            padding: '6px 12px',
            borderRadius: '6px',
            fontSize: '11px',
            color: 'var(--text-secondary)'
          }}>
            {currentPhoto.title}
          </div>
        </div>

        {/* Render Bounding Boxes */}
        {showOverlays && currentPhoto.boxes.map((box) => {
          const isSelected = selectedBoxId === box.id || (!selectedBoxId && box.id === currentPhoto.boxes[0]?.id)
          const borderColor = box.severity === 'ALERT' ? '#f43f5e' : box.severity === 'SEVERE' ? '#ef4444' : box.severity === 'MODERATE' ? '#f59e0b' : '#10b981'

          return (
            <div
              key={box.id}
              onClick={() => setSelectedBoxId(box.id)}
              style={{
                position: 'absolute',
                top: box.top,
                left: box.left,
                width: box.width,
                height: box.height,
                border: `2px solid ${borderColor}`,
                background: isSelected ? 'rgba(239, 68, 68, 0.2)' : 'rgba(239, 68, 68, 0.08)',
                boxShadow: isSelected ? `0 0 15px ${borderColor}` : `0 0 8px rgba(0, 0, 0, 0.5)`,
                cursor: 'pointer',
                transition: 'all 0.2s ease',
                borderRadius: '6px'
              }}
            >
              {/* Tag Pin */}
              <div style={{
                position: 'absolute',
                top: '-26px',
                left: '-2px',
                background: borderColor,
                color: '#fff',
                fontSize: '10px',
                fontWeight: 700,
                padding: '2px 8px',
                borderRadius: '4px',
                display: 'flex',
                alignItems: 'center',
                gap: '4px',
                whiteSpace: 'nowrap',
                boxShadow: '0 2px 8px rgba(0,0,0,0.4)'
              }}>
                <span>{box.confidence}%</span>
                <span>{box.labelUa}</span>
              </div>

              {/* Corner crosshairs */}
              <div style={{ position: 'absolute', top: '-4px', left: '-4px', width: '8px', height: '8px', borderTop: `2px solid ${borderColor}`, borderLeft: `2px solid ${borderColor}` }} />
              <div style={{ position: 'absolute', top: '-4px', right: '-4px', width: '8px', height: '8px', borderTop: `2px solid ${borderColor}`, borderRight: `2px solid ${borderColor}` }} />
              <div style={{ position: 'absolute', bottom: '-4px', left: '-4px', width: '8px', height: '8px', borderBottom: `2px solid ${borderColor}`, borderLeft: `2px solid ${borderColor}` }} />
              <div style={{ position: 'absolute', bottom: '-4px', right: '-4px', width: '8px', height: '8px', borderBottom: `2px solid ${borderColor}`, borderRight: `2px solid ${borderColor}` }} />
            </div>
          )
        })}

        {/* Selected Box Interactive Bottom Card */}
        {showOverlays && selectedBox && (
          <div style={{
            position: 'absolute',
            bottom: '12px',
            left: '12px',
            right: '12px',
            background: 'rgba(13, 18, 29, 0.92)',
            backdropFilter: 'blur(12px)',
            border: '1px solid rgba(0, 210, 255, 0.3)',
            borderRadius: '10px',
            padding: '12px 16px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            gap: '16px'
          }}>
            <div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '4px' }}>
                <span style={{
                  background: selectedBox.severity === 'ALERT' ? '#f43f5e' : selectedBox.severity === 'SEVERE' ? '#ef4444' : selectedBox.severity === 'MODERATE' ? '#f59e0b' : '#10b981',
                  color: '#fff',
                  fontSize: '10px',
                  fontWeight: 700,
                  padding: '1px 6px',
                  borderRadius: '4px'
                }}>
                  {selectedBox.confidence}% Confidence
                </span>
                <strong style={{ fontSize: '13px', color: '#fff' }}>{selectedBox.labelUa}</strong>
                <span className="mono" style={{ fontSize: '11px', color: '#93c5fd' }}>OEM: {selectedBox.oemNumber}</span>
              </div>
              <p style={{ fontSize: '12px', color: 'var(--text-secondary)', margin: 0 }}>
                {selectedBox.descriptionUa}
              </p>
            </div>

            <a
              href={`https://avto.pro/search/?q=${encodeURIComponent(`${make} ${model} ${selectedBox.labelUa}`)}`}
              target="_blank"
              rel="noopener noreferrer"
              className="btn-secondary"
              style={{ fontSize: '11px', padding: '6px 12px', whiteSpace: 'nowrap' }}
            >
              <span>Знайти на Avto.pro</span>
              <ChevronRight size={14} />
            </a>
          </div>
        )}
      </div>

      {/* Angle Thumbnails Selector */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '10px', marginBottom: '18px' }}>
        {photoAngles.map((p, idx) => {
          const isActive = activePhotoIndex === idx
          return (
            <div
              key={p.id}
              onClick={() => {
                setActivePhotoIndex(idx)
                setSelectedBoxId(null)
              }}
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: '10px',
                padding: '8px 12px',
                borderRadius: '10px',
                background: isActive ? 'rgba(0, 210, 255, 0.12)' : 'rgba(255, 255, 255, 0.02)',
                border: isActive ? '1px solid var(--accent-cyan)' : '1px solid var(--border)',
                cursor: 'pointer',
                transition: 'all 0.15s ease'
              }}
            >
              <img
                src={p.url}
                alt={p.title}
                style={{ width: '48px', height: '36px', objectFit: 'cover', borderRadius: '6px' }}
              />
              <div style={{ overflow: 'hidden' }}>
                <div style={{ fontSize: '12px', fontWeight: 600, color: isActive ? '#fff' : 'var(--text-secondary)', whiteSpace: 'nowrap', textOverflow: 'ellipsis' }}>
                  {p.title}
                </div>
                <div style={{ fontSize: '10px', color: 'var(--accent-cyan)' }}>
                  {p.badge} ({p.boxes.length} {p.boxes.length === 1 ? 'зона' : 'зони'})
                </div>
              </div>
            </div>
          )
        })}
      </div>

      {/* Summary Box */}
      <div style={{
        background: 'rgba(0, 210, 255, 0.05)',
        border: '1px solid rgba(0, 210, 255, 0.15)',
        padding: '14px 18px',
        borderRadius: '12px',
        marginBottom: '16px',
        fontSize: '13px',
        lineHeight: 1.5
      }}>
        {inspection.inspectionSummaryUa}
      </div>

      {/* Airbags & Detected Zones Count */}
      <div style={{ display: 'flex', gap: '16px', alignItems: 'center', marginBottom: '16px', flexWrap: 'wrap' }}>
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
          <span>{inspection.airbagsDeployed ? 'Airbags: Спрацювали подушки (SRS відновлення)' : 'Airbags: Салон і подушки цілі'}</span>
        </div>

        <div style={{ fontSize: '13px', color: 'var(--text-muted)' }}>
          Ідентифіковано дефектів: <strong style={{ color: 'var(--text-primary)' }}>{inspection.detectedZones.length}</strong>
        </div>
      </div>

      {/* Model specific domain heuristics */}
      {inspection.modelSpecificRisks && inspection.modelSpecificRisks.length > 0 && (
        <div style={{ marginTop: '12px' }}>
          <div style={{ fontSize: '12px', fontWeight: 600, color: 'var(--accent-amber)', marginBottom: '6px', display: 'flex', alignItems: 'center', gap: '6px' }}>
            <AlertTriangle size={14} />
            <span>Специфіка моделі та ризики прихованих вузлів (Domain Heuristics):</span>
          </div>
          <ul style={{ listStyle: 'none', paddingLeft: 0, margin: 0 }}>
            {inspection.modelSpecificRisks.map((risk, idx) => (
              <li key={idx} style={{
                fontSize: '12px',
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
