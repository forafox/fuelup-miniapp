package ru.fuelup.order.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.fuelup.common.order.OrderStatus;

import java.util.UUID;

@Data
@AllArgsConstructor
public class OrderHistory {
    private UUID historyId;
    private UUID orderId;
    private OrderStatus previousStatus;
    private OrderStatus newStatus;
    private String comment;
    private Long timestamp;

    public static OrderHistory transition(UUID orderId, OrderStatus from, OrderStatus to) {
        return new OrderHistory(null, orderId, from, to, null, System.currentTimeMillis());
    }
}
