package ru.fuelup.order.usecase.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import ru.fuelup.common.annotations.Loggable;
import ru.fuelup.common.order.OrderStatus;
import ru.fuelup.order.usecase.OrderStatusChangedEvent;
import ru.fuelup.order.usecase.ProcessPartnerCallback;
import ru.fuelup.order.usecase.port.OrderRepository;

@Slf4j
@RequiredArgsConstructor
public class ProcessPartnerCallbackImpl implements ProcessPartnerCallback {

    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Loggable
    public void invoke(CallbackCommand command) {
        var order = orderRepository.findByPartnerOrderId(command.partnerOrderId());
        if (order.isEmpty()) {
            log.warn("Callback for unknown partnerOrderId={}", command.partnerOrderId());
            return;
        }

        var current = order.get();
        if (current.isTerminal()) {
            log.info("Ignoring callback for terminal order={} partnerOrderId={}",
                    current.getOrderId(), command.partnerOrderId());
            return;
        }

        var nextStatus = mapPartnerStatus(command.newStatus());

        if (nextStatus == OrderStatus.COMPLETED && command.actualAmount() != null) {
            orderRepository.updateActualFueling(
                    current.getOrderId(),
                    command.actualAmount(),
                    command.actualSum() != null ? command.actualSum() : 0.0,
                    OrderStatus.COMPLETED
            );
        } else {
            orderRepository.updateStatus(
                    current.getOrderId(),
                    nextStatus,
                    command.failReason()
            );
        }

        log.info("Processed partner callback: partnerOrderId={} -> status={}",
                command.partnerOrderId(), nextStatus);

        eventPublisher.publishEvent(new OrderStatusChangedEvent(
                this,
                current.getOrderId(),
                nextStatus,
                command.actualAmount(),
                command.actualSum()
        ));
    }

    private OrderStatus mapPartnerStatus(String partnerStatus) {
        return switch (partnerStatus) {
            case "ACCEPTED", "IN_PROGRESS" -> OrderStatus.PAID;
            case "COMPLETED", "DISPENSED" -> OrderStatus.COMPLETED;
            case "CANCELLED" -> OrderStatus.CANCELLED;
            default -> {
                log.warn("Unknown partner status: {}", partnerStatus);
                yield OrderStatus.FAILED;
            }
        };
    }
}
