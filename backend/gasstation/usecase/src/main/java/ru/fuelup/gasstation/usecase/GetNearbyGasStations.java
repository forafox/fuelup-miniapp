package ru.fuelup.gasstation.usecase;

import ru.fuelup.gasstation.domain.GasStation;

import java.util.List;
import java.util.UUID;

public interface GetNearbyGasStations {
    List<GasStation> invoke(
            double latitude,
            double longitude,
            int radiusMeters,
            String brandCode,
            String fuelType,
            UUID customerId
    );
}
