package ru.fuelup.order.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.fuelup.common.order.OrderPaymentType;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class CreateOrderRequest {
    private UUID gasStationId;
    private Integer columnNumber;
    private String fuelType;
    private Double requestedAmount;
    /** Цена топлива, которую клиент видел при выборе — для проверки актуальности */
    private Double clientFuelPrice;
    private OrderPaymentType paymentType;
    private UUID sbpSubscriptionId;
    private UUID promoCodeId;
    private Boolean useBonus;
}
