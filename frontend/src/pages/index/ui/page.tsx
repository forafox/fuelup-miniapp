import { useState } from 'react'
import type { GasStation } from '@/entities/gas-station'
import { useNearbyGasStations } from '@/features/gas-stations/api/gasStationApi'
import { useGeolocation } from '@/shared/lib/useGeolocation'
import { GasStationFlow } from '@/widgets/gas-station-flow'
import { BonusButton } from '@/features/bonus-system/ui/BonusButton'

export function IndexPage() {
  const { position, error: geoError } = useGeolocation()
  const [selectedStation, setSelectedStation] = useState<GasStation | null>(null)

  const { data: stations, isLoading } = useNearbyGasStations(
    {
      latitude: position?.latitude ?? 59.9343,
      longitude: position?.longitude ?? 30.3351,
      radiusMeters: 5000,
    },
    position != null
  )

  if (selectedStation) {
    return (
      <GasStationFlow
        station={selectedStation}
        onClose={() => setSelectedStation(null)}
      />
    )
  }

  return (
    <div className="relative flex h-screen flex-col">
      <div className="absolute right-4 top-4 z-10">
        <BonusButton />
      </div>

      {/* В реальном приложении здесь Yandex Maps с маркерами АЗС */}
      <div className="flex-1 bg-muted/30 flex items-center justify-center">
        {isLoading && <p className="text-muted-foreground text-sm">Загружаем АЗС рядом...</p>}
        {geoError && (
          <p className="text-muted-foreground text-sm">
            Разрешите доступ к геолокации для отображения ближайших АЗС
          </p>
        )}
      </div>

      <div className="border-t border-border bg-background p-4">
        <h2 className="mb-2 text-sm font-medium text-muted-foreground">
          Ближайшие АЗС ({stations?.length ?? 0})
        </h2>
        <div className="flex flex-col gap-2">
          {stations?.slice(0, 5).map((station) => (
            <button
              key={station.id}
              onClick={() => setSelectedStation(station)}
              className="flex items-center justify-between rounded-xl border border-border bg-background px-3 py-2 text-left hover:bg-accent"
            >
              <div>
                <p className="font-medium text-sm">{station.name}</p>
                <p className="text-xs text-muted-foreground">{station.address}</p>
              </div>
              {station.distanceMeters && (
                <span className="text-xs text-muted-foreground">
                  {formatDistance(station.distanceMeters)}
                </span>
              )}
            </button>
          ))}
        </div>
      </div>
    </div>
  )
}

function formatDistance(meters: number) {
  return meters >= 1000 ? `${(meters / 1000).toFixed(1)} км` : `${meters} м`
}
