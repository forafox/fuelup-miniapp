package ru.fuelup.order.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.fuelup.common.order.OrderStatus;
import ru.fuelup.order.persistence.model.OrderModel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderJpaRepository extends JpaRepository<OrderModel, UUID> {

    Optional<OrderModel> findByPartnerOrderId(UUID partnerOrderId);

    List<OrderModel> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);

    @Query("SELECT COUNT(o) FROM OrderModel o WHERE o.customerId = :customerId AND o.createdAt >= :sinceMs")
    long countByCustomerIdAndCreatedAtGreaterThanEqual(
            @Param("customerId") UUID customerId,
            @Param("sinceMs") long sinceMs
    );

    @Modifying
    @Query("UPDATE OrderModel o SET o.status = :status, o.failReason = :reason, o.updatedAt = :now WHERE o.id = :id")
    int updateStatus(
            @Param("id") UUID id,
            @Param("status") OrderStatus status,
            @Param("reason") String reason,
            @Param("now") long now
    );

    @Modifying
    @Query("UPDATE OrderModel o SET o.paymentUrl = :url, o.updatedAt = :now WHERE o.id = :id")
    int updatePaymentUrl(@Param("id") UUID id, @Param("url") String url, @Param("now") long now);

    @Modifying
    @Query("""
            UPDATE OrderModel o
            SET o.actualAmount = :amount, o.actualSum = :sum, o.status = :status, o.updatedAt = :now
            WHERE o.id = :id
            """)
    int updateActualFueling(
            @Param("id") UUID id,
            @Param("amount") double amount,
            @Param("sum") double sum,
            @Param("status") OrderStatus status,
            @Param("now") long now
    );
}
