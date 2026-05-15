import { useState } from 'react'

interface Filters {
  brandCode: string | null
  fuelType: string | null
  sbpOnly: boolean
}

interface Props {
  brands: Array<{ code: string; name: string }>
  fuelTypes: string[]
  onChange: (filters: Filters) => void
}

export function FiltersPanel({ brands, fuelTypes, onChange }: Props) {
  const [filters, setFilters] = useState<Filters>({
    brandCode: null,
    fuelType: null,
    sbpOnly: false,
  })

  const update = (patch: Partial<Filters>) => {
    const next = { ...filters, ...patch }
    setFilters(next)
    onChange(next)
  }

  const hasActiveFilters = filters.brandCode || filters.fuelType || filters.sbpOnly

  return (
    <div className="flex flex-col gap-3 p-4">
      <div className="flex items-center justify-between">
        <h3 className="text-sm font-semibold">Фильтры</h3>
        {hasActiveFilters && (
          <button
            onClick={() => update({ brandCode: null, fuelType: null, sbpOnly: false })}
            className="text-xs text-primary"
          >
            Сбросить
          </button>
        )}
      </div>

      <section>
        <p className="mb-1.5 text-xs text-muted-foreground">Бренд АЗС</p>
        <div className="flex flex-wrap gap-2">
          {brands.map((brand) => (
            <button
              key={brand.code}
              onClick={() => update({ brandCode: filters.brandCode === brand.code ? null : brand.code })}
              className={`rounded-full border px-3 py-1 text-xs font-medium transition-colors ${
                filters.brandCode === brand.code
                  ? 'border-primary bg-primary text-primary-foreground'
                  : 'border-border bg-background text-foreground hover:bg-accent'
              }`}
            >
              {brand.name}
            </button>
          ))}
        </div>
      </section>

      <section>
        <p className="mb-1.5 text-xs text-muted-foreground">Тип топлива</p>
        <div className="flex flex-wrap gap-2">
          {fuelTypes.map((type) => (
            <button
              key={type}
              onClick={() => update({ fuelType: filters.fuelType === type ? null : type })}
              className={`rounded-full border px-3 py-1 text-xs font-medium transition-colors ${
                filters.fuelType === type
                  ? 'border-primary bg-primary text-primary-foreground'
                  : 'border-border bg-background text-foreground hover:bg-accent'
              }`}
            >
              {type}
            </button>
          ))}
        </div>
      </section>

      <label className="flex items-center gap-2.5">
        <input
          type="checkbox"
          checked={filters.sbpOnly}
          onChange={(e) => update({ sbpOnly: e.target.checked })}
          className="h-4 w-4 rounded border-border accent-primary"
        />
        <span className="text-sm">Только с оплатой СБП</span>
      </label>
    </div>
  )
}
