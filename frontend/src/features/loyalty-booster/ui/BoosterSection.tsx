import { useQuery } from '@tanstack/react-query'
import { apiClient } from '@/shared/api/client'

interface Booster {
  id: string
  name: string
  multiplier: number
  description: string
  validUntil: string
  isActive: boolean
}

// Бустер — временный множитель начисления бонусов (напр. ×2 в выходные)
export function BoosterSection() {
  const { data: boosters } = useQuery({
    queryKey: ['loyalty', 'boosters'],
    queryFn: () => apiClient.get<Booster[]>('/loyalty/boosters/active').then((r) => r.data),
  })

  if (!boosters?.length) return null

  return (
    <section className="px-4">
      <h3 className="mb-2 text-sm font-medium text-muted-foreground">Активные бустеры</h3>
      <div className="flex flex-col gap-2">
        {boosters.map((booster) => (
          <div
            key={booster.id}
            className="flex items-center gap-3 rounded-xl border border-amber-200 bg-amber-50 p-3"
          >
            <span className="text-2xl">🚀</span>
            <div>
              <p className="text-sm font-semibold">{booster.name}</p>
              <p className="text-xs text-muted-foreground">
                ×{booster.multiplier} к бонусам · до {new Date(booster.validUntil).toLocaleDateString('ru-RU')}
              </p>
            </div>
          </div>
        ))}
      </div>
    </section>
  )
}
