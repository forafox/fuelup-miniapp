package ru.fuelup.order.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.fuelup.common.order.OrderPaymentType;
import ru.fuelup.common.order.OrderStatus;
import ru.fuelup.common.platform.Platform;

import java.util.UUID;

@Data
@AllArgsConstructor
public class Order {
    private UUID orderId;
    private UUID partnerOrderId;
    private String paymentUrl;
    private OrderStatus status;
    private String failReason;
    private OrderPaymentType paymentType;
    private Double requestedAmount;
    private Double actualAmount;
    private Double requestedSum;
    private Double actualSum;
    private Double fuelPrice;
    private Double discountedFuelPrice;
    private Double partnerClientPrice;
    private String fuelType;
    private UUID customerId;
    private UUID gasStationId;
    private Long columnId;
    private Integer columnNumber;
    private Long nozzleId;
    private Long createdAt;
    private Long updatedAt;
    private Boolean isTest;
    private Platform platform;
    private UUID sbpSubscriptionId;
    private UUID boosterId;
    private UUID campaignId;
    private UUID promoCodeId;

    /** Создание нового заказа из команды пользователя */
    public Order(
            UUID customerId,
            Double requestedAmount,
            Double fuelPrice,
            Double discountedFuelPrice,
            Double partnerClientPrice,
            String fuelType,
            UUID gasStationId,
            Integer columnNumber,
            OrderPaymentType paymentType,
            Boolean isTest,
            Platform platform,
            UUID sbpSubscriptionId
    ) {
        this.customerId = customerId;
        this.requestedAmount = requestedAmount;
        this.fuelPrice = fuelPrice;
        this.discountedFuelPrice = discountedFuelPrice;
        this.partnerClientPrice = partnerClientPrice;
        this.fuelType = fuelType;
        this.gasStationId = gasStationId;
        this.columnNumber = columnNumber;
        this.paymentType = paymentType;
        this.isTest = isTest;
        this.platform = platform;
        this.sbpSubscriptionId = sbpSubscriptionId;
    }

    /** Обновление после callback от партнёра (фактический объём топлива) */
    public Order(
            UUID partnerOrderId,
            OrderPaymentType paymentType,
            Double actualAmount,
            Double actualSum,
            Long updatedAt,
            OrderStatus status
    ) {
        this.partnerOrderId = partnerOrderId;
        this.paymentType = paymentType;
        this.actualAmount = actualAmount;
        this.actualSum = actualSum;
        this.updatedAt = updatedAt;
        this.status = status;
    }

    /** Результат размещения заказа в партнёрской системе */
    public Order(UUID partnerOrderId, UUID orderId, String paymentUrl, boolean successful) {
        this.partnerOrderId = partnerOrderId;
        this.orderId = orderId;
        this.paymentUrl = paymentUrl;
        this.status = successful ? OrderStatus.PLACED : OrderStatus.FAILED;
    }

    public boolean isTerminal() {
        return status == OrderStatus.COMPLETED
                || status == OrderStatus.CANCELLED
                || status == OrderStatus.FAILED;
    }
}
