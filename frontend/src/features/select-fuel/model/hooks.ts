import type { Fuel } from '@/entities/gas-station'
import { useSelectFuelContext } from './context'

export function useOnSelectFuel() {
  return useSelectFuelContext().selectFuel
}

export function useSelectedFuel() {
  return useSelectFuelContext().selectedFuel
}

export function useIsFuelSelected(fuel: Fuel) {
  const { selectedFuel } = useSelectFuelContext()
  return selectedFuel?.type === fuel.type
}
