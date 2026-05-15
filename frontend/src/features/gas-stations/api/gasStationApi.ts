import { useQuery } from '@tanstack/react-query'
import { apiClient } from '@/shared/api/client'
import type { GasStation } from '@/entities/gas-station'

interface NearbyParams {
  latitude: number
  longitude: number
  radiusMeters?: number
  brandCode?: string
  fuelType?: string
}

const gasStationApi = {
  getNearby: (params: NearbyParams) =>
    apiClient
      .get<{ stations: GasStation[] }>('/gas-stations', { params })
      .then((r) => r.data.stations),

  getById: (id: string) =>
    apiClient.get<GasStation>(`/gas-stations/${id}`).then((r) => r.data),
}

export function useNearbyGasStations(params: NearbyParams, enabled = true) {
  return useQuery({
    queryKey: ['gas-stations', 'nearby', params],
    queryFn: () => gasStationApi.getNearby(params),
    enabled,
    staleTime: 60_000,
  })
}

export function useGasStationById(id: string | null) {
  return useQuery({
    queryKey: ['gas-stations', id],
    queryFn: () => gasStationApi.getById(id!),
    enabled: id != null,
  })
}
