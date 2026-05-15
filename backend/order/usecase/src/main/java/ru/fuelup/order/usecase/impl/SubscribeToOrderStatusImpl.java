package ru.fuelup.order.usecase.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.fuelup.order.usecase.OrderStatusChangedEvent;
import ru.fuelup.order.usecase.SubscribeToOrderStatus;
import ru.fuelup.order.usecase.port.OrderRepository;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscribeToOrderStatusImpl implements SubscribeToOrderStatus {

    private final Map<UUID, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final OrderRepository orderRepository;

    @Override
    public void subscribe(UUID orderId, UUID customerId, SseEmitter emitter) {
        var order = orderRepository.findById(orderId);
        if (order.isEmpty() || !order.get().getCustomerId().equals(customerId)) {
            emitter.completeWithError(new IllegalArgumentException("order not found"));
            return;
        }

        emitters.put(orderId, emitter);
        emitter.onCompletion(() -> emitters.remove(orderId, emitter));
        emitter.onTimeout(() -> {
            emitters.remove(orderId, emitter);
            emitter.complete();
        });

        // push current status immediately so the client doesn't have to wait
        sendEvent(emitter, orderId, order.get().getStatus().name(), null, null);

        if (order.get().isTerminal()) {
            emitter.complete();
            emitters.remove(orderId, emitter);
        }
    }

    @EventListener
    public void onStatusChanged(OrderStatusChangedEvent event) {
        var emitter = emitters.get(event.getOrderId());
        if (emitter == null) return;

        sendEvent(emitter, event.getOrderId(), event.getStatus().name(),
                event.getActualAmount(), event.getActualSum());

        if (event.getStatus().name().matches("COMPLETED|CANCELLED|FAILED")) {
            emitter.complete();
            emitters.remove(event.getOrderId(), emitter);
        }
    }

    private void sendEvent(SseEmitter emitter, UUID orderId, String status,
                           Double actualAmount, Double actualSum) {
        try {
            var builder = SseEmitter.event()
                    .name("order-status")
                    .data(buildPayload(orderId, status, actualAmount, actualSum));
            emitter.send(builder);
        } catch (IOException e) {
            log.debug("SSE send failed for orderId={}: {}", orderId, e.getMessage());
            emitters.remove(orderId, emitter);
        }
    }

    private String buildPayload(UUID orderId, String status, Double actualAmount, Double actualSum) {
        var sb = new StringBuilder("{");
        sb.append("\"orderId\":\"").append(orderId).append("\"");
        sb.append(",\"status\":\"").append(status).append("\"");
        if (actualAmount != null) sb.append(",\"actualAmount\":").append(actualAmount);
        if (actualSum != null) sb.append(",\"actualSum\":").append(actualSum);
        sb.append("}");
        return sb.toString();
    }
}
