import { useState } from 'react'
import type { GasStation } from '@/entities/gas-station'
import { SelectFuelProvider } from '@/features/select-fuel'
import { SelectFuelItem } from '@/features/select-fuel/ui/SelectFuelItem'
import { useSelectedFuel } from '@/features/select-fuel/model/hooks'
import { useCreateOrder } from '@/features/create-order/model/useCreateOrder'
import { OrderStatusBanner } from './OrderStatusBanner'

interface Props {
  station: GasStation
  onClose: () => void
}

export function GasStationFlow({ station, onClose }: Props) {
  return (
    <SelectFuelProvider>
      <GasStationFlowInner station={station} onClose={onClose} />
    </SelectFuelProvider>
  )
}

function GasStationFlowInner({ station, onClose }: Props) {
  const [columnNumber, setColumnNumber] = useState<number | null>(null)
  const [amount, setAmount] = useState<number | ''>('')
  const selectedFuel = useSelectedFuel()
  const { createOrder, isPending, activeOrder, orderStatus } = useCreateOrder()

  const handleSubmit = () => {
    if (!selectedFuel || !columnNumber || !amount) return

    createOrder({
      gasStationId: station.id,
      columnNumber,
      fuelType: selectedFuel.type,
      requestedAmount: Number(amount),
      clientFuelPrice: selectedFuel.clientPrice,
      paymentType: 'SBP',
    })
  }

  if (activeOrder && orderStatus) {
    return <OrderStatusBanner order={activeOrder} status={orderStatus} onClose={onClose} />
  }

  return (
    <div className="flex flex-col gap-4 p-4">
      <div>
        <h2 className="text-lg font-semibold">{station.name}</h2>
        <p className="text-sm text-muted-foreground">{station.address}</p>
      </div>

      <section>
        <h3 className="mb-2 text-sm font-medium text-muted-foreground">Выберите топливо</h3>
        <div className="flex flex-col gap-1">
          {station.fuels.map((fuel) => (
            <SelectFuelItem key={fuel.type} fuel={fuel} />
          ))}
        </div>
      </section>

      <section>
        <h3 className="mb-2 text-sm font-medium text-muted-foreground">Колонка</h3>
        <div className="flex flex-wrap gap-2">
          {station.columns
            .filter((c) => c.status !== 'OFFLINE')
            .map((col) => (
              <button
                key={col.id}
                onClick={() => setColumnNumber(col.number)}
                className={`rounded-lg border px-4 py-2 text-sm font-medium transition-colors ${
                  columnNumber === col.number
                    ? 'border-primary bg-primary text-primary-foreground'
                    : 'border-border bg-background hover:bg-accent'
                }`}
              >
                {col.number}
              </button>
            ))}
        </div>
      </section>

      <section>
        <h3 className="mb-2 text-sm font-medium text-muted-foreground">Объём, л</h3>
        <input
          type="number"
          min={1}
          max={200}
          value={amount}
          onChange={(e) => setAmount(e.target.value ? Number(e.target.value) : '')}
          placeholder="Введите объём"
          className="w-full rounded-lg border border-border bg-background px-3 py-2 text-sm"
        />
        {selectedFuel && amount !== '' && (
          <p className="mt-1 text-sm text-muted-foreground">
            Итого:{' '}
            <span className="font-semibold text-foreground">
              {(selectedFuel.clientPrice * Number(amount)).toFixed(2)} ₽
            </span>
          </p>
        )}
      </section>

      <button
        onClick={handleSubmit}
        disabled={!selectedFuel || !columnNumber || !amount || isPending}
        className="w-full rounded-xl bg-primary py-3 text-sm font-semibold text-primary-foreground transition-opacity disabled:opacity-50"
      >
        {isPending ? 'Оформляем...' : 'Оплатить через СБП'}
      </button>
    </div>
  )
}
