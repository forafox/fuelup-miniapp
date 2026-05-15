import { useEffect, useState } from 'react'
import type { Order, OrderStatus } from '@/entities/order'

interface Props {
  order: Order
  status: OrderStatus
  onClose: () => void
}

const STATUS_CONFIG: Record<OrderStatus, { label: string; description: string; icon: string; color: string }> = {
  PENDING:   { label: 'Оформляем заказ',    description: 'Подключаемся к колонке...',              icon: '⏳', color: 'bg-amber-50 border-amber-200' },
  PLACED:    { label: 'Ожидаем оплату',     description: 'Нажмите кнопку для оплаты через СБП',    icon: '💳', color: 'bg-blue-50 border-blue-200' },
  PAID:      { label: 'Оплата получена',    description: 'Подъезжайте к колонке',                  icon: '✅', color: 'bg-green-50 border-green-200' },
  COMPLETED: { label: 'Заправка завершена', description: 'Спасибо! Бонусы начислены',              icon: '⛽', color: 'bg-green-50 border-green-200' },
  CANCELLED: { label: 'Заказ отменён',      description: 'Средства возвращены',                    icon: '❌', color: 'bg-gray-50 border-gray-200' },
  FAILED:    { label: 'Ошибка',             description: 'Что-то пошло не так. Попробуйте снова',  icon: '⚠️', color: 'bg-red-50 border-red-200' },
}

const TERMINAL_STATUSES: OrderStatus[] = ['COMPLETED', 'CANCELLED', 'FAILED']

export function ActiveOrderBanner({ order, status, onClose }: Props) {
  const [elapsed, setElapsed] = useState(0)
  const config = STATUS_CONFIG[status]
  const isTerminal = TERMINAL_STATUSES.includes(status)

  useEffect(() => {
    if (isTerminal) return
    const id = setInterval(() => setElapsed((e) => e + 1), 1000)
    return () => clearInterval(id)
  }, [isTerminal])

  const formatElapsed = (s: number) => `${Math.floor(s / 60)}:${String(s % 60).padStart(2, '0')}`

  return (
    <div className={`rounded-2xl border-2 p-5 ${config.color}`}>
      <div className="flex items-start gap-3">
        <span className="text-3xl">{config.icon}</span>
        <div className="flex-1">
          <p className="font-semibold text-foreground">{config.label}</p>
          <p className="mt-0.5 text-sm text-muted-foreground">{config.description}</p>
          {!isTerminal && elapsed > 0 && (
            <p className="mt-1 text-xs text-muted-foreground">Прошло: {formatElapsed(elapsed)}</p>
          )}
        </div>
        {isTerminal && (
          <button onClick={onClose} className="text-muted-foreground hover:text-foreground">
            ✕
          </button>
        )}
      </div>

      {status === 'PLACED' && order.paymentUrl && (
        <a
          href={order.paymentUrl}
          target="_blank"
          rel="noopener noreferrer"
          className="mt-4 flex w-full items-center justify-center rounded-xl bg-primary py-3 text-sm font-semibold text-primary-foreground"
        >
          Оплатить через СБП
        </a>
      )}

      {status === 'COMPLETED' && order.actualAmount != null && (
        <div className="mt-3 rounded-xl bg-white/60 p-3 text-sm">
          <span className="text-muted-foreground">Отпущено: </span>
          <span className="font-semibold">{order.actualAmount.toFixed(2)} л</span>
          {order.actualSum != null && (
            <>
              <span className="text-muted-foreground"> · Сумма: </span>
              <span className="font-semibold">{order.actualSum.toFixed(2)} ₽</span>
            </>
          )}
        </div>
      )}
    </div>
  )
}
