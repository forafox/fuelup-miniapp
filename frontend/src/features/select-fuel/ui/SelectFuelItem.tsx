import { useIsFuelSelected, useOnSelectFuel } from '../model/hooks'
import type { Fuel } from '@/entities/gas-station'
import { SelectItem } from '@/shared/ui/select-item'

interface Props {
  fuel: Fuel
}

export function SelectFuelItem({ fuel }: Props) {
  const onSelect = useOnSelectFuel()
  const isSelected = useIsFuelSelected(fuel)

  const price = fuel.discountedPrice ?? fuel.clientPrice

  return (
    <SelectItem
      id={fuel.type}
      title={fuel.localizedName}
      selected={isSelected}
      onClick={() => onSelect(fuel)}
    >
      <span className="font-medium text-foreground">{fuel.localizedName}</span>
      <div className="flex items-center gap-1.5">
        {fuel.discountedPrice && (
          <span className="text-xs text-muted-foreground line-through">
            {formatPrice(fuel.basePrice)}
          </span>
        )}
        <span className="text-sm font-semibold text-primary">{formatPrice(price)}</span>
      </div>
    </SelectItem>
  )
}

function formatPrice(price: number) {
  return new Intl.NumberFormat('ru-RU', {
    style: 'currency',
    currency: 'RUB',
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  }).format(price)
}
