import { useState } from 'react'
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { apiClient } from '@/shared/api/client'

const STEPS = [
  {
    id: 'welcome',
    title: 'Заправляйтесь без очередей',
    body: 'Выберите АЗС на карте, выберите колонку и оплатите через СБП — всё прямо в мессенджере.',
    icon: '⛽',
  },
  {
    id: 'payment',
    title: 'Любой банк — через СБП',
    body: 'Оплата проходит через Систему быстрых платежей. Никакой привязки к конкретному банку.',
    icon: '💳',
  },
  {
    id: 'loyalty',
    title: 'Бонусы за каждую заправку',
    body: 'Копите бонусы и тратьте их на следующие заправки. 1 бонус = 1 рубль.',
    icon: '🎁',
  },
  {
    id: 'location',
    title: 'Разрешите геолокацию',
    body: 'Нам нужен доступ к вашему местоположению, чтобы показывать ближайшие АЗС.',
    icon: '📍',
    action: 'request_location',
  },
] as const

interface Props {
  onComplete: () => void
}

export function OnboardingFlow({ onComplete }: Props) {
  const [step, setStep] = useState(0)
  const queryClient = useQueryClient()

  const completeMutation = useMutation({
    mutationFn: () => apiClient.post('/customers/me/onboarding/complete'),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['customer', 'profile'] })
      onComplete()
    },
  })

  const current = STEPS[step]
  const isLast = step === STEPS.length - 1

  const handleNext = async () => {
    if (current.action === 'request_location') {
      navigator.geolocation?.getCurrentPosition(() => {}, () => {})
    }
    if (isLast) {
      completeMutation.mutate()
    } else {
      setStep((s) => s + 1)
    }
  }

  return (
    <div className="flex min-h-screen flex-col items-center justify-between p-8">
      <div className="flex gap-1.5 pt-2">
        {STEPS.map((_, i) => (
          <div
            key={i}
            className={`h-1.5 rounded-full transition-all ${
              i === step ? 'w-6 bg-primary' : i < step ? 'w-3 bg-primary/40' : 'w-3 bg-border'
            }`}
          />
        ))}
      </div>

      <div className="flex flex-col items-center gap-6 text-center">
        <span className="text-7xl">{current.icon}</span>
        <h2 className="text-2xl font-bold tracking-tight">{current.title}</h2>
        <p className="text-base text-muted-foreground leading-relaxed">{current.body}</p>
      </div>

      <div className="w-full">
        <button
          onClick={handleNext}
          disabled={completeMutation.isPending}
          className="w-full rounded-2xl bg-primary py-4 text-base font-semibold text-primary-foreground disabled:opacity-60"
        >
          {isLast
            ? completeMutation.isPending ? 'Загружаем...' : 'Начать'
            : 'Далее'}
        </button>
        {step > 0 && (
          <button
            onClick={() => setStep((s) => s - 1)}
            className="mt-3 w-full py-2 text-sm text-muted-foreground"
          >
            Назад
          </button>
        )}
      </div>
    </div>
  )
}
