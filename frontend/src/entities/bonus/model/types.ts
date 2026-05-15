export interface BonusBalance {
  balance: number
  pendingBalance: number
  totalEarned: number
  totalSpent: number
}

export interface BonusTransaction {
  id: string
  type: BonusTransactionType
  amount: number
  description: string
  orderId: string | null
  timestamp: number
}

export type BonusTransactionType =
  | 'ACCRUAL'
  | 'WITHDRAWAL'
  | 'WELCOME_BONUS'
  | 'REFERRAL_BONUS'
  | 'EXPIRATION'

export interface Booster {
  id: string
  name: string
  description: string
  multiplier: number
  brandCode: string | null
  fuelGroup: string | null
  validFrom: string
  validUntil: string
  isActive: boolean
}
