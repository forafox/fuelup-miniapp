package ru.fuelup.order.usecase.port;

import ru.fuelup.common.order.OrderStatus;
import ru.fuelup.order.domain.Order;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    Order save(Order order);

    Optional<Order> findById(UUID orderId);

    Optional<Order> findByPartnerOrderId(UUID partnerOrderId);

    void updateStatus(UUID orderId, OrderStatus status, String reason);

    void updatePartnerData(Order order);

    void updatePaymentUrl(UUID orderId, String paymentUrl);

    void updateActualFueling(UUID orderId, double actualAmount, double actualSum, OrderStatus status);

    long countTodayOrdersByCustomer(UUID customerId);
}
