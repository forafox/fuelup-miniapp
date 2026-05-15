package ru.fuelup.order.response;

import lombok.Builder;
import lombok.Getter;
import ru.fuelup.common.order.OrderStatus;
import ru.fuelup.order.usecase.result.OrderResult;

import java.util.UUID;

@Getter
@Builder
public class CreateOrderResponse {
    private UUID orderId;
    private UUID partnerOrderId;
    private String paymentUrl;
    private OrderStatus status;
    private Double requestedSum;
    private Double fuelPrice;
    private Double discountedFuelPrice;
    private String errorCode;
    private Double actualPriceIfMismatch;

    public static CreateOrderResponse from(OrderResult result) {
        return CreateOrderResponse.builder()
                .orderId(result.getOrderId())
                .partnerOrderId(result.getPartnerOrderId())
                .paymentUrl(result.getPaymentUrl())
                .status(result.getStatus())
                .requestedSum(result.getRequestedSum())
                .fuelPrice(result.getFuelPrice())
                .discountedFuelPrice(result.getDiscountedFuelPrice())
                .build();
    }

    public static CreateOrderResponse priceMismatch(double actualPrice) {
        return CreateOrderResponse.builder()
                .errorCode("PRICE_MISMATCH")
                .actualPriceIfMismatch(actualPrice)
                .build();
    }

    public static CreateOrderResponse limitExceeded() {
        return CreateOrderResponse.builder()
                .errorCode("DAILY_LIMIT_EXCEEDED")
                .build();
    }
}
