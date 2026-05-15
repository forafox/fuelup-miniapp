export type OrderStatus =
  | 'PENDING'
  | 'PLACED'
  | 'PAID'
  | 'COMPLETED'
  | 'CANCELLED'
  | 'FAILED'

export type OrderPaymentType = 'SBP' | 'SBP_SUBSCRIPTION' | 'BONUS'

export interface Order {
  orderId: string
  partnerOrderId: string
  paymentUrl: string | null
  status: OrderStatus
  fuelType: string
  requestedAmount: number
  actualAmount: number | null
  requestedSum: number
  actualSum: number | null
  fuelPrice: number
  discountedFuelPrice: number | null
  paymentType: OrderPaymentType
  gasStationId: string
  columnNumber: number
  createdAt: number
  updatedAt: number
}

export type OrderStatusEvent = {
  orderId: string
  status: OrderStatus
  actualAmount?: number
  actualSum?: number
}
