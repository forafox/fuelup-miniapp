package ru.fuelup.gasstation.usecase.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.fuelup.gasstation.domain.GasStation;
import ru.fuelup.gasstation.usecase.GetNearbyGasStations;
import ru.fuelup.gasstation.usecase.port.GasStationCachePort;
import ru.fuelup.gasstation.usecase.port.GasStationRepository;
import ru.fuelup.gasstation.usecase.port.PriceCalculatorPort;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class GetNearbyGasStationsImpl implements GetNearbyGasStations {

    private final GasStationRepository repository;
    private final GasStationCachePort cache;
    private final PriceCalculatorPort priceCalculator;

    @Override
    public List<GasStation> invoke(
            double latitude, double longitude,
            int radiusMeters,
            String brandCode, String fuelType,
            UUID customerId
    ) {
        var cacheKey = buildCacheKey(latitude, longitude, radiusMeters, brandCode, fuelType);

        return cache.get(cacheKey)
                .map(stations -> enrichWithPersonalPrices(stations, customerId))
                .orElseGet(() -> {
                    var stations = repository.findNearby(latitude, longitude, radiusMeters, brandCode, fuelType);
                    cache.put(cacheKey, stations);
                    return enrichWithPersonalPrices(stations, customerId);
                });
    }

    private List<GasStation> enrichWithPersonalPrices(List<GasStation> stations, UUID customerId) {
        return stations.stream()
                .map(station -> priceCalculator.applyPersonalDiscounts(station, customerId))
                .toList();
    }

    private String buildCacheKey(double lat, double lon, int radius, String brand, String fuel) {
        // округляем до 3 знаков (~100м точность) для эффективного кэширования
        return "stations:%.3f:%.3f:%d:%s:%s".formatted(lat, lon, radius,
                brand != null ? brand : "all",
                fuel != null ? fuel : "all");
    }
}
