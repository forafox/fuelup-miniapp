import { useState, useEffect } from 'react'

interface Position {
  latitude: number
  longitude: number
}

export function useGeolocation() {
  const [position, setPosition] = useState<Position | null>(null)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (!navigator.geolocation) {
      setError('Геолокация не поддерживается браузером')
      return
    }

    const id = navigator.geolocation.watchPosition(
      (pos) => {
        setPosition({
          latitude: pos.coords.latitude,
          longitude: pos.coords.longitude,
        })
        setError(null)
      },
      (err) => {
        setError(err.message)
      },
      { enableHighAccuracy: true, maximumAge: 30_000 }
    )

    return () => navigator.geolocation.clearWatch(id)
  }, [])

  return { position, error }
}
