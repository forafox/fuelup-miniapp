import { useQuery } from '@tanstack/react-query'
import { apiClient } from '@/shared/api/client'
import type { BonusBalance, BonusTransaction } from '@/entities/bonus/model/types'

async function fetchBalance(): Promise<BonusBalance> {
  const { data } = await apiClient.get<BonusBalance>('/loyalty/balance')
  return data
}

async function fetchTransactions(): Promise<BonusTransaction[]> {
  const { data } = await apiClient.get<BonusTransaction[]>('/loyalty/transactions')
  return data
}

export function useBonusBalance() {
  return useQuery({
    queryKey: ['bonus-balance'],
    queryFn: fetchBalance,
    staleTime: 60_000,
  })
}

export function useBonusTransactions() {
  return useQuery({
    queryKey: ['bonus-transactions'],
    queryFn: fetchTransactions,
    staleTime: 60_000,
  })
}
