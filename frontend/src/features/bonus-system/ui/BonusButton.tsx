import { useQuery } from '@tanstack/react-query'
import { apiClient } from '@/shared/api/client'

interface BonusBalance {
  balance: number
  pendingBalance: number
}

export function BonusButton() {
  const { data } = useQuery({
    queryKey: ['bonus', 'balance'],
    queryFn: () => apiClient.get<BonusBalance>('/loyalty/balance').then((r) => r.data),
  })

  if (!data) return null

  return (
    <button className="flex items-center gap-2 rounded-full border border-border bg-background px-3 py-1.5 text-sm">
      <CoinIcon className="h-4 w-4 text-amber-500" />
      <span className="font-semibold">{data.balance}</span>
    </button>
  )
}

function CoinIcon({ className }: { className?: string }) {
  return (
    <svg className={className} viewBox="0 0 24 24" fill="currentColor">
      <circle cx="12" cy="12" r="10" />
      <text x="12" y="16" textAnchor="middle" fontSize="11" fill="white" fontWeight="bold">₽</text>
    </svg>
  )
}
