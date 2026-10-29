import { FullVehicleAuditReport, PricingRates } from './types'

const API_BASE = '/api/v1'

export const api = {
  async runAudit(vin: string, auctionPriceUsd: number, isFloodVehicle: boolean = false): Promise<FullVehicleAuditReport> {
    const res = await fetch(`${API_BASE}/audit/run`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ vin, auctionPriceUsd, isFloodVehicle })
    })
    if (!res.ok) throw new Error('Failed to run vehicle audit')
    return res.json()
  },

  async runDemoTeslaX(): Promise<FullVehicleAuditReport> {
    const res = await fetch(`${API_BASE}/audit/demo-tesla-x`)
    if (!res.ok) throw new Error('Failed to fetch demo audit')
    return res.json()
  },

  async getEffectivePricing(userId?: number): Promise<PricingRates> {
    const url = userId ? `${API_BASE}/pricing/effective?userId=${userId}` : `${API_BASE}/pricing/effective`
    const res = await fetch(url)
    if (!res.ok) throw new Error('Failed to load pricing')
    return res.json()
  },

  async updatePricing(userId: number, rates: Partial<PricingRates>): Promise<PricingRates> {
    const res = await fetch(`${API_BASE}/pricing/my-rates?userId=${userId}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(rates)
    })
    if (!res.ok) throw new Error('Failed to update pricing')
    return res.json()
  },

  async resetPricing(userId: number): Promise<PricingRates> {
    const res = await fetch(`${API_BASE}/pricing/reset-defaults?userId=${userId}`, {
      method: 'POST'
    })
    if (!res.ok) throw new Error('Failed to reset pricing')
    return res.json()
  },

  async getAuditHistory(userId?: number): Promise<any[]> {
    const url = userId ? `${API_BASE}/audit/history?userId=${userId}` : `${API_BASE}/audit/history`
    const res = await fetch(url)
    if (!res.ok) return []
    return res.json()
  }
}
