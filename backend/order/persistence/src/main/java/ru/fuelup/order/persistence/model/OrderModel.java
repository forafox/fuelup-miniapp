package ru.fuelup.order.persistence.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.fuelup.common.order.OrderPaymentType;
import ru.fuelup.common.order.OrderStatus;
import ru.fuelup.common.platform.Platform;
import ru.fuelup.order.domain.Order;

import java.util.UUID;

@Entity
@Table(name = "fueling_order", indexes = {
        @Index(name = "idx_order_customer", columnList = "customer_id"),
        @Index(name = "idx_order_partner", columnList = "partner_order_id"),
        @Index(name = "idx_order_status", columnList = "status"),
})
@Getter
@Setter
@NoArgsConstructor
public class OrderModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "partner_order_id")
    private UUID partnerOrderId;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "gas_station_id", nullable = false)
    private UUID gasStationId;

    @Column(name = "column_id")
    private Long columnId;

    @Column(name = "column_number", nullable = false)
    private Integer columnNumber;

    @Column(name = "nozzle_id")
    private Long nozzleId;

    @Column(name = "fuel_type", nullable = false, length = 20)
    private String fuelType;

    @Column(name = "requested_amount", nullable = false)
    private Double requestedAmount;

    @Column(name = "actual_amount")
    private Double actualAmount;

    @Column(name = "requested_sum")
    private Double requestedSum;

    @Column(name = "actual_sum")
    private Double actualSum;

    @Column(name = "fuel_price", nullable = false)
    private Double fuelPrice;

    @Column(name = "discounted_fuel_price")
    private Double discountedFuelPrice;

    @Column(name = "partner_client_price")
    private Double partnerClientPrice;

    @Column(name = "payment_url", length = 1000)
    private String paymentUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false, length = 30)
    private OrderPaymentType paymentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatus status;

    @Column(name = "fail_reason", length = 255)
    private String failReason;

    @Column(name = "sbp_subscription_id")
    private UUID sbpSubscriptionId;

    @Column(name = "promo_code_id")
    private UUID promoCodeId;

    @Column(name = "booster_id")
    private UUID boosterId;

    @Column(name = "campaign_id")
    private UUID campaignId;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform", nullable = false, length = 20)
    private Platform platform;

    @Column(name = "is_test", nullable = false)
    private Boolean isTest = false;

    @Column(name = "created_at", nullable = false)
    private Long createdAt;

    @Column(name = "updated_at", nullable = false)
    private Long updatedAt;

    public Order toDomain() {
        return new Order(
                id, partnerOrderId, paymentUrl, status, failReason,
                paymentType, requestedAmount, actualAmount,
                requestedSum, actualSum, fuelPrice, discountedFuelPrice,
                partnerClientPrice, fuelType, customerId, gasStationId,
                columnId, columnNumber, nozzleId,
                createdAt, updatedAt, isTest, platform, sbpSubscriptionId,
                boosterId, campaignId, promoCodeId
        );
    }

    public static OrderModel fromDomain(Order order) {
        var model = new OrderModel();
        model.setPartnerOrderId(order.getPartnerOrderId());
        model.setCustomerId(order.getCustomerId());
        model.setGasStationId(order.getGasStationId());
        model.setColumnNumber(order.getColumnNumber());
        model.setFuelType(order.getFuelType());
        model.setRequestedAmount(order.getRequestedAmount());
        model.setFuelPrice(order.getFuelPrice());
        model.setDiscountedFuelPrice(order.getDiscountedFuelPrice());
        model.setPartnerClientPrice(order.getPartnerClientPrice());
        model.setPaymentType(order.getPaymentType());
        model.setStatus(order.getStatus() != null ? order.getStatus() : ru.fuelup.common.order.OrderStatus.PENDING);
        model.setPlatform(order.getPlatform());
        model.setIsTest(order.getIsTest() != null ? order.getIsTest() : false);
        model.setSbpSubscriptionId(order.getSbpSubscriptionId());
        model.setPromoCodeId(order.getPromoCodeId());
        model.setCreatedAt(order.getCreatedAt() != null ? order.getCreatedAt() : System.currentTimeMillis());
        model.setUpdatedAt(System.currentTimeMillis());
        return model;
    }
}
