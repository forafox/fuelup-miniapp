package ru.fuelup.order.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.fuelup.common.platform.PrincipalInfo;
import ru.fuelup.order.request.CreateOrderRequest;
import ru.fuelup.order.response.CreateOrderResponse;
import ru.fuelup.order.response.OrderHistoryResponse;
import ru.fuelup.order.usecase.CreateOrder;
import ru.fuelup.order.usecase.GetOrderById;
import ru.fuelup.order.usecase.GetOrderHistoryByCustomer;
import ru.fuelup.order.usecase.command.OrderCommand;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderEndpoint {

    private final CreateOrder createOrder;
    private final GetOrderById getOrderById;
    private final GetOrderHistoryByCustomer getOrderHistory;

    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(
            @RequestBody CreateOrderRequest request,
            @AuthenticationPrincipal PrincipalInfo principal
    ) {
        var command = OrderCommand.builder()
                .customerId(principal.getCustomerId())
                .gasStationId(request.getGasStationId())
                .columnNumber(request.getColumnNumber())
                .fuelType(request.getFuelType())
                .requestedAmount(request.getRequestedAmount())
                .clientFuelPrice(request.getClientFuelPrice())
                .paymentType(request.getPaymentType())
                .sbpSubscriptionId(request.getSbpSubscriptionId())
                .promoCodeId(request.getPromoCodeId())
                .useBonus(request.getUseBonus())
                .platform(principal.getPlatform())
                .isTest(false)
                .build();

        return createOrder.invoke(command).fold(
                error -> switch (error) {
                    case CreateOrder.CreateOrderError.PriceMismatchError e ->
                            ResponseEntity.unprocessableEntity()
                                    .body(CreateOrderResponse.priceMismatch(e.getActual()));
                    case CreateOrder.CreateOrderError.DailyLimitExceededError e ->
                            ResponseEntity.status(429).body(CreateOrderResponse.limitExceeded());
                    default -> ResponseEntity.internalServerError().build();
                },
                result -> ResponseEntity.ok(CreateOrderResponse.from(result))
        );
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<CreateOrderResponse> getOrder(
            @PathVariable UUID orderId,
            @AuthenticationPrincipal PrincipalInfo principal
    ) {
        return getOrderById.invoke(orderId, principal.getCustomerId())
                .map(order -> ResponseEntity.ok(CreateOrderResponse.from(order)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/history")
    public ResponseEntity<List<OrderHistoryResponse>> getHistory(
            @AuthenticationPrincipal PrincipalInfo principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var history = getOrderHistory.invoke(principal.getCustomerId(), page, size);
        return ResponseEntity.ok(history);
    }
}
