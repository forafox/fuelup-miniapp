package ru.fuelup.order.usecase;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import ru.fuelup.common.order.OrderStatus;

import java.util.UUID;

@Getter
public class OrderStatusChangedEvent extends ApplicationEvent {

    private final UUID orderId;
    private final OrderStatus status;
    private final Double actualAmount;
    private final Double actualSum;

    public OrderStatusChangedEvent(Object source, UUID orderId, OrderStatus status,
                                   Double actualAmount, Double actualSum) {
        super(source);
        this.orderId = orderId;
        this.status = status;
        this.actualAmount = actualAmount;
        this.actualSum = actualSum;
    }
}
