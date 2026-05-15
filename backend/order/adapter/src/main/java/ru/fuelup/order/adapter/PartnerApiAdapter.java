package ru.fuelup.order.adapter;

import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.fuelup.order.usecase.port.PartnerApiPort;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PartnerApiAdapter implements PartnerApiPort {

    private final PartnerFeignClient feignClient;

    @Override
    public Either<String, PartnerCreateResult> createFuelingOrder(
            UUID internalOrderId,
            UUID gasStationId,
            Integer columnNumber,
            String fuelType,
            Double requestedAmount,
            Double discountedPrice
    ) {
        try {
            var request = new PartnerCreateRequest(
                    internalOrderId.toString(),
                    gasStationId.toString(),
                    columnNumber,
                    fuelType,
                    requestedAmount,
                    discountedPrice
            );
            var response = feignClient.createOrder(request);
            return Either.right(new PartnerCreateResult(
                    UUID.fromString(response.orderId()),
                    response.columnId(),
                    response.nozzleId()
            ));
        } catch (Exception e) {
            log.error("Partner API error creating order: internalId={} station={}: {}",
                    internalOrderId, gasStationId, e.getMessage());
            return Either.left(e.getMessage());
        }
    }

    @FeignClient(name = "partner-api", url = "${fuelup.partner.api-url}")
    interface PartnerFeignClient {
        @PostMapping("/v2/orders")
        PartnerOrderResponse createOrder(@RequestBody PartnerCreateRequest request);
    }

    record PartnerCreateRequest(
            String internalRef,
            String stationId,
            Integer columnNumber,
            String fuelType,
            Double amount,
            Double price
    ) {}

    record PartnerOrderResponse(
            String orderId,
            Long columnId,
            Long nozzleId,
            String status
    ) {}
}
