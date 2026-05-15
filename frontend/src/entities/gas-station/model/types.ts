export type GasStationStatus = 'ACTIVE' | 'INACTIVE' | 'MAINTENANCE'
export type GasStationServiceType = 'CLASSIC' | 'SELF_SERVICE'

export interface Fuel {
  type: string
  localizedName: string
  basePrice: number
  discountedPrice: number | null
  clientPrice: number
}

export interface Column {
  id: number
  number: number
  status: 'FREE' | 'BUSY' | 'OFFLINE'
  availableFuelTypes: string[]
}

export interface GasStation {
  id: string
  brandCode: string
  name: string
  address: string
  latitude: number
  longitude: number
  status: GasStationStatus
  serviceType: GasStationServiceType
  fuels: Fuel[]
  columns: Column[]
  discountApplicable: boolean
  sbpEnabled: boolean
  distanceMeters?: number
}
