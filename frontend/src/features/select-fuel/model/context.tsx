import { createContext, useContext, useState, PropsWithChildren } from 'react'
import type { Fuel } from '@/entities/gas-station'

interface SelectFuelContextValue {
  selectedFuel: Fuel | null
  selectFuel: (fuel: Fuel) => void
  clearFuel: () => void
}

const SelectFuelContext = createContext<SelectFuelContextValue | null>(null)

export function SelectFuelProvider({ children }: PropsWithChildren) {
  const [selectedFuel, setSelectedFuel] = useState<Fuel | null>(null)

  return (
    <SelectFuelContext.Provider
      value={{
        selectedFuel,
        selectFuel: setSelectedFuel,
        clearFuel: () => setSelectedFuel(null),
      }}
    >
      {children}
    </SelectFuelContext.Provider>
  )
}

export const useSelectFuelContext = () => {
  const ctx = useContext(SelectFuelContext)
  if (!ctx) throw new Error('useSelectFuelContext must be inside SelectFuelProvider')
  return ctx
}
