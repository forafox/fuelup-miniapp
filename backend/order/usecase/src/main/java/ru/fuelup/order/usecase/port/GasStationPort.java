package ru.fuelup.order.usecase.port;

import lombok.Value;

import java.util.Optional;
import java.util.UUID;

public interface GasStationPort {

    Optional<FuelInfo> getFuelPrice(UUID stationId, String fuelType);

    boolean isStationActive(UUID stationId);

    @Value
    class FuelInfo {
        String fuelType;
        Double basePrice;
        Double discountedPrice;
        Double clientPrice;
    }
}
