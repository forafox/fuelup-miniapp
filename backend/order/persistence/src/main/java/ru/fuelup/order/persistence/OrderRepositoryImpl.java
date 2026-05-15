package ru.fuelup.order.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.fuelup.common.order.OrderStatus;
import ru.fuelup.order.domain.Order;
import ru.fuelup.order.persistence.model.OrderModel;
import ru.fuelup.order.persistence.repository.OrderJpaRepository;
import ru.fuelup.order.usecase.port.OrderRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository jpa;

    @Override
    @Transactional
    public Order save(Order order) {
        var model = OrderModel.fromDomain(order);
        var saved = jpa.save(model);
        return saved.toDomain();
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        return jpa.findById(orderId).map(OrderModel::toDomain);
    }

    @Override
    public Optional<Order> findByPartnerOrderId(UUID partnerOrderId) {
        return jpa.findByPartnerOrderId(partnerOrderId).map(OrderModel::toDomain);
    }

    @Override
    @Transactional
    public void updateStatus(UUID orderId, OrderStatus status, String reason) {
        jpa.updateStatus(orderId, status, reason, System.currentTimeMillis());
    }

    @Override
    @Transactional
    public void updatePartnerData(Order order) {
        // обновление через save — допустимо т.к. orderId уже есть
        jpa.findById(order.getOrderId()).ifPresent(model -> {
            model.setPartnerOrderId(order.getPartnerOrderId());
            model.setColumnId(order.getColumnId());
            model.setNozzleId(order.getNozzleId());
            model.setStatus(order.getStatus());
            model.setUpdatedAt(System.currentTimeMillis());
            jpa.save(model);
        });
    }

    @Override
    @Transactional
    public void updatePaymentUrl(UUID orderId, String paymentUrl) {
        jpa.updatePaymentUrl(orderId, paymentUrl, System.currentTimeMillis());
    }

    @Override
    @Transactional
    public void updateActualFueling(UUID orderId, double actualAmount, double actualSum, OrderStatus status) {
        jpa.updateActualFueling(orderId, actualAmount, actualSum, status, System.currentTimeMillis());
    }

    @Override
    public long countTodayOrdersByCustomer(UUID customerId) {
        long startOfDayMs = LocalDate.now(ZoneId.of("Europe/Moscow"))
                .atStartOfDay(ZoneId.of("Europe/Moscow"))
                .toInstant()
                .toEpochMilli();
        return jpa.countByCustomerIdAndCreatedAtGreaterThanEqual(customerId, startOfDayMs);
    }
}
