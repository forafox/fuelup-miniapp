import { useParams } from '@tanstack/react-router'
import { useOrder } from '@/features/order-history/api/orderHistoryApi'
import type { OrderStatus } from '@/entities/order/model/types'

const STATUS_LABEL: Record<OrderStatus, string> = {
  PENDING: 'Ожидает подтверждения',
  PLACED: 'Ожидает оплаты',
  PAID: 'Оплачен — заправка идёт',
  COMPLETED: 'Завершён',
  CANCELLED: 'Отменён',
  FAILED: 'Ошибка',
}

const STATUS_COLOR: Record<OrderStatus, string> = {
  PENDING: 'text-yellow-600',
  PLACED: 'text-blue-600',
  PAID: 'text-blue-700',
  COMPLETED: 'text-green-600',
  CANCELLED: 'text-muted-foreground',
  FAILED: 'text-destructive',
}

export function OrderPage() {
  const { orderId } = useParams({ from: '/orders/$orderId' })
  const { data: order, isLoading, isError } = useOrder(orderId)

  if (isLoading) {
    return (
      <div className="flex h-screen items-center justify-center">
        <p className="text-muted-foreground text-sm">Загрузка заказа...</p>
      </div>
    )
  }

  if (isError || !order) {
    return (
      <div className="flex h-screen items-center justify-center">
        <p className="text-destructive text-sm">Заказ не найден</p>
      </div>
    )
  }

  return (
    <div className="flex flex-col h-screen bg-background">
      <header className="border-b border-border px-4 py-3">
        <p className="text-xs text-muted-foreground">Заказ</p>
        <p className="font-mono text-sm font-medium">#{order.orderId.slice(-8).toUpperCase()}</p>
      </header>

      <div className="flex-1 overflow-y-auto p-4 space-y-4">
        <section className="rounded-xl border border-border bg-card p-4 space-y-3">
          <div className="flex justify-between items-center">
            <span className="text-sm text-muted-foreground">Статус</span>
            <span className={`text-sm font-medium ${STATUS_COLOR[order.status]}`}>
              {STATUS_LABEL[order.status]}
            </span>
          </div>
          <div className="flex justify-between items-center">
            <span className="text-sm text-muted-foreground">Топливо</span>
            <span className="text-sm font-medium">{order.fuelType}</span>
          </div>
          <div className="flex justify-between items-center">
            <span className="text-sm text-muted-foreground">Заказано</span>
            <span className="text-sm font-medium">{order.requestedAmount} л</span>
          </div>
          {order.actualAmount != null && (
            <div className="flex justify-between items-center">
              <span className="text-sm text-muted-foreground">Отпущено</span>
              <span className="text-sm font-medium text-green-600">{order.actualAmount} л</span>
            </div>
          )}
          <div className="flex justify-between items-center">
            <span className="text-sm text-muted-foreground">Цена за литр</span>
            <span className="text-sm font-medium">
              {order.discountedFuelPrice != null
                ? `${formatPrice(order.discountedFuelPrice)} ₽`
                : `${formatPrice(order.fuelPrice)} ₽`}
            </span>
          </div>
          <div className="border-t border-border pt-3 flex justify-between items-center">
            <span className="text-sm font-medium">Итого</span>
            <span className="text-base font-semibold">
              {formatPrice(order.actualSum ?? order.requestedSum)} ₽
            </span>
          </div>
        </section>

        {order.paymentUrl && order.status === 'PLACED' && (
          <a
            href={order.paymentUrl}
            className="block w-full rounded-xl bg-primary py-3 text-center text-sm font-medium text-primary-foreground"
          >
            Оплатить через СБП
          </a>
        )}

        <p className="text-center text-xs text-muted-foreground">
          {new Date(order.createdAt).toLocaleString('ru-RU')}
        </p>
      </div>
    </div>
  )
}

function formatPrice(value: number) {
  return value.toLocaleString('ru-RU', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}
