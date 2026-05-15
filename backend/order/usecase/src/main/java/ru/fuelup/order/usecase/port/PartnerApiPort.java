package ru.fuelup.order.usecase.port;

import io.vavr.control.Either;
import lombok.Value;

import java.util.UUID;

public interface PartnerApiPort {

    Either<String, PartnerCreateResult> createFuelingOrder(
            UUID internalOrderId,
            UUID gasStationId,
            Integer columnNumber,
            String fuelType,
            Double requestedAmount,
            Double discountedPrice
    );

    @Value
    class PartnerCreateResult {
        UUID partnerOrderId;
        Long columnId;
        Long nozzleId;
    }
}
