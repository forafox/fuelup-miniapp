import { useState } from 'react'
import { useMutation } from '@tanstack/react-query'
import { apiClient } from '@/shared/api/client'

// Жизненный цикл заказа автомойки отличается от АЗС:
// нет SSE, статус обновляется по pull (polling) т.к. мойка — синхронная услуга
interface CarWashStation {
  id: string
  name: string
  address: string
  isOpen: boolean
  availableBoxes: number
  programs: Array<{ id: number; name: string; price: number; discountedPrice: number | null; durationMinutes: number }>
  boxes: Array<{ id: number; number: number; status: 'FREE' | 'BUSY' | 'OFFLINE'; type: string }>
}

interface Props {
  station: CarWashStation
  onClose: () => void
}

export function CarWashFlow({ station, onClose }: Props) {
  const [selectedProgram, setSelectedProgram] = useState<CarWashStation['programs'][0] | null>(null)
  const [boxNumber, setBoxNumber] = useState<number | null>(null)
  const [result, setResult] = useState<{ paymentUrl: string; orderId: string } | null>(null)

  const mutation = useMutation({
    mutationFn: () =>
      apiClient
        .post('/carwashes/orders', {
          stationId: station.id,
          boxNumber,
          programId: selectedProgram!.id,
        })
        .then((r) => r.data),
    onSuccess: (data) => setResult({ paymentUrl: data.paymentUrl, orderId: data.orderId }),
  })

  const price = selectedProgram?.discountedPrice ?? selectedProgram?.price

  if (result) {
    return (
      <div className="flex flex-col gap-4 p-6 text-center">
        <span className="text-5xl">🚿</span>
        <h2 className="text-xl font-bold">Заказ оформлен!</h2>
        <p className="text-sm text-muted-foreground">Оплатите через СБП и заезжайте в бокс №{boxNumber}</p>
        {result.paymentUrl && (
          <a
            href={result.paymentUrl}
            target="_blank"
            rel="noreferrer"
            className="rounded-xl bg-primary py-3 text-sm font-semibold text-primary-foreground"
          >
            Оплатить через СБП
          </a>
        )}
        <button onClick={onClose} className="text-sm text-muted-foreground">
          Закрыть
        </button>
      </div>
    )
  }

  return (
    <div className="flex flex-col gap-4 p-4">
      <div>
        <h2 className="text-lg font-semibold">{station.name}</h2>
        <p className="text-sm text-muted-foreground">{station.address}</p>
        {!station.isOpen && (
          <p className="mt-1 text-xs font-medium text-destructive">Сейчас закрыто</p>
        )}
      </div>

      <section>
        <h3 className="mb-2 text-sm font-medium text-muted-foreground">Программа мойки</h3>
        <div className="flex flex-col gap-2">
          {station.programs.map((p) => (
            <button
              key={p.id}
              onClick={() => setSelectedProgram(p)}
              className={`flex items-center justify-between rounded-xl border px-3 py-2.5 text-left transition-colors ${
                selectedProgram?.id === p.id
                  ? 'border-primary bg-primary/10'
                  : 'border-border bg-background hover:bg-accent'
              }`}
            >
              <div>
                <p className="text-sm font-medium">{p.name}</p>
                <p className="text-xs text-muted-foreground">{p.durationMinutes} мин</p>
              </div>
              <div className="text-right">
                {p.discountedPrice && (
                  <p className="text-xs text-muted-foreground line-through">{p.price} ₽</p>
                )}
                <p className="text-sm font-semibold text-primary">
                  {p.discountedPrice ?? p.price} ₽
                </p>
              </div>
            </button>
          ))}
        </div>
      </section>

      <section>
        <h3 className="mb-2 text-sm font-medium text-muted-foreground">Бокс</h3>
        <div className="flex flex-wrap gap-2">
          {station.boxes
            .filter((b) => b.status !== 'OFFLINE')
            .map((box) => (
              <button
                key={box.id}
                onClick={() => setBoxNumber(box.number)}
                disabled={box.status === 'BUSY'}
                className={`rounded-lg border px-4 py-2 text-sm font-medium transition-colors disabled:opacity-40 ${
                  boxNumber === box.number
                    ? 'border-primary bg-primary text-primary-foreground'
                    : 'border-border bg-background hover:bg-accent'
                }`}
              >
                Бокс {box.number}
                {box.status === 'BUSY' && <span className="ml-1 text-xs">(занят)</span>}
              </button>
            ))}
        </div>
      </section>

      {price && selectedProgram && (
        <p className="text-sm text-muted-foreground">
          Итого: <span className="font-semibold text-foreground">{price} ₽</span>
        </p>
      )}

      <button
        onClick={() => mutation.mutate()}
        disabled={!selectedProgram || !boxNumber || mutation.isPending || !station.isOpen}
        className="w-full rounded-xl bg-primary py-3 text-sm font-semibold text-primary-foreground disabled:opacity-50"
      >
        {mutation.isPending ? 'Оформляем...' : 'Перейти к оплате'}
      </button>
    </div>
  )
}
