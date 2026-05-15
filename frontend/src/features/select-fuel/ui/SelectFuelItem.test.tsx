import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { vi } from 'vitest'
import { SelectFuelItem } from './SelectFuelItem'
import { SelectFuelProvider } from '../model/context'
import type { Fuel } from '@/entities/gas-station'

const mockFuel: Fuel = {
  type: 'AI95',
  localizedName: 'АИ-95',
  basePrice: 60.5,
  discountedPrice: 57.8,
  clientPrice: 57.8,
}

const mockFuelNoDiscount: Fuel = {
  type: 'DT',
  localizedName: 'Дизель',
  basePrice: 72.0,
  discountedPrice: null,
  clientPrice: 72.0,
}

function renderWithProvider(ui: React.ReactElement) {
  return render(<SelectFuelProvider>{ui}</SelectFuelProvider>)
}

describe('SelectFuelItem', () => {
  it('renders fuel name and discounted price', () => {
    renderWithProvider(<SelectFuelItem fuel={mockFuel} />)
    expect(screen.getByText('АИ-95')).toBeInTheDocument()
    expect(screen.getByText(/57,80/)).toBeInTheDocument()
  })

  it('shows strikethrough base price when discount exists', () => {
    renderWithProvider(<SelectFuelItem fuel={mockFuel} />)
    expect(screen.getByText(/60,50/)).toHaveClass('line-through')
  })

  it('does not show strikethrough when no discount', () => {
    renderWithProvider(<SelectFuelItem fuel={mockFuelNoDiscount} />)
    expect(screen.queryByText(/line-through/)).not.toBeInTheDocument()
  })

  it('becomes selected on click', async () => {
    const user = userEvent.setup()
    renderWithProvider(<SelectFuelItem fuel={mockFuel} />)
    const btn = screen.getByRole('option')
    expect(btn).toHaveAttribute('aria-selected', 'false')
    await user.click(btn)
    expect(btn).toHaveAttribute('aria-selected', 'true')
  })
})
