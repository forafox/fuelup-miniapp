package ru.fuelup.order.usecase.result;

import lombok.Builder;
import lombok.Getter;
import ru.fuelup.common.order.OrderStatus;

import java.util.UUID;

@Getter
@Builder
public class OrderResult {
    private final UUID orderId;
    private final UUID partnerOrderId;
    private final String paymentUrl;
    private final OrderStatus status;
    private final Double requestedSum;
    private final Double fuelPrice;
    private final Double discountedFuelPrice;
}
