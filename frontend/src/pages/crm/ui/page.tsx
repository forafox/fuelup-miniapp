import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { apiClient } from '@/shared/api/client'

type CrmTab = 'orders' | 'customers' | 'promo'

interface OrderRow {
  orderId: string
  customerId: string
  gasStationName: string
  fuelType: string
  requestedAmount: number
  actualAmount: number | null
  requestedSum: number
  status: string
  createdAt: number
  platform: string
}

export function CrmPage() {
  const [tab, setTab] = useState<CrmTab>('orders')
  const [statusFilter, setStatusFilter] = useState<string>('ALL')

  const { data: orders, isLoading } = useQuery({
    queryKey: ['crm', 'orders', statusFilter],
    queryFn: () =>
      apiClient
        .get<{ content: OrderRow[]; totalElements: number }>('/crm/orders', {
          params: { status: statusFilter === 'ALL' ? undefined : statusFilter, size: 50 },
        })
        .then((r) => r.data),
    refetchInterval: 30_000,
  })

  return (
    <div className="min-h-screen bg-background">
      <header className="border-b border-border bg-background px-6 py-4">
        <h1 className="text-xl font-bold">FuelUp CRM</h1>
      </header>

      <div className="flex border-b border-border">
        {(['orders', 'customers', 'promo'] as CrmTab[]).map((t) => (
          <button
            key={t}
            onClick={() => setTab(t)}
            className={`px-6 py-3 text-sm font-medium transition-colors ${
              tab === t
                ? 'border-b-2 border-primary text-primary'
                : 'text-muted-foreground hover:text-foreground'
            }`}
          >
            {{ orders: 'Заказы', customers: 'Клиенты', promo: 'Промокоды' }[t]}
          </button>
        ))}
      </div>

      <main className="p-6">
        {tab === 'orders' && (
          <>
            <div className="mb-4 flex gap-2">
              {['ALL', 'PENDING', 'PLACED', 'COMPLETED', 'FAILED', 'CANCELLED'].map((s) => (
                <button
                  key={s}
                  onClick={() => setStatusFilter(s)}
                  className={`rounded-full border px-3 py-1 text-xs font-medium ${
                    statusFilter === s
                      ? 'border-primary bg-primary text-primary-foreground'
                      : 'border-border bg-background hover:bg-accent'
                  }`}
                >
                  {s}
                </button>
              ))}
            </div>

            {isLoading ? (
              <p className="text-muted-foreground text-sm">Загрузка...</p>
            ) : (
              <div className="overflow-x-auto rounded-xl border border-border">
                <table className="w-full text-sm">
                  <thead className="bg-muted/50">
                    <tr>
                      {['ID', 'АЗС', 'Топливо', 'Объём', 'Сумма', 'Статус', 'Платформа', 'Дата'].map((h) => (
                        <th key={h} className="px-4 py-3 text-left text-xs font-medium text-muted-foreground">
                          {h}
                        </th>
                      ))}
                    </tr>
                  </thead>
                  <tbody>
                    {orders?.content.map((order) => (
                      <tr key={order.orderId} className="border-t border-border hover:bg-muted/30">
                        <td className="px-4 py-3 font-mono text-xs">{order.orderId.slice(-8)}</td>
                        <td className="px-4 py-3">{order.gasStationName}</td>
                        <td className="px-4 py-3">{order.fuelType}</td>
                        <td className="px-4 py-3">
                          {order.actualAmount?.toFixed(1) ?? order.requestedAmount.toFixed(1)} л
                        </td>
                        <td className="px-4 py-3">{order.requestedSum.toFixed(0)} ₽</td>
                        <td className="px-4 py-3">
                          <StatusBadge status={order.status} />
                        </td>
                        <td className="px-4 py-3 text-xs">{order.platform}</td>
                        <td className="px-4 py-3 text-xs">
                          {new Date(order.createdAt).toLocaleString('ru-RU', {
                            day: '2-digit', month: '2-digit', hour: '2-digit', minute: '2-digit',
                          })}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
                <div className="border-t border-border px-4 py-2 text-xs text-muted-foreground">
                  Всего: {orders?.totalElements ?? 0}
                </div>
              </div>
            )}
          </>
        )}

        {tab === 'customers' && (
          <p className="text-muted-foreground">Раздел клиентов в разработке</p>
        )}
        {tab === 'promo' && (
          <p className="text-muted-foreground">Управление промокодами в разработке</p>
        )}
      </main>
    </div>
  )
}

function StatusBadge({ status }: { status: string }) {
  const config: Record<string, string> = {
    COMPLETED: 'bg-green-100 text-green-700',
    PAID:      'bg-green-50 text-green-600',
    PLACED:    'bg-blue-100 text-blue-700',
    PENDING:   'bg-amber-100 text-amber-700',
    FAILED:    'bg-red-100 text-red-700',
    CANCELLED: 'bg-gray-100 text-gray-600',
  }
  return (
    <span className={`rounded-full px-2 py-0.5 text-xs font-medium ${config[status] ?? 'bg-gray-100 text-gray-600'}`}>
      {status}
    </span>
  )
}
