package ru.fuelup.order.usecase;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

public interface SubscribeToOrderStatus {
    void subscribe(UUID orderId, UUID customerId, SseEmitter emitter);
}
