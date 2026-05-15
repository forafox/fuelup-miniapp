package ru.fuelup.order.usecase.command;

import lombok.Builder;
import lombok.Getter;
import ru.fuelup.common.order.OrderPaymentType;
import ru.fuelup.common.platform.Platform;

import java.util.UUID;

@Getter
@Builder
public class OrderCommand {
    private final UUID customerId;
    private final UUID gasStationId;
    private final Integer columnNumber;
    private final String fuelType;
    private final Double requestedAmount;
    /** Цена, которую видел пользователь в момент оформления — для проверки актуальности */
    private final Double clientFuelPrice;
    private final OrderPaymentType paymentType;
    private final UUID sbpSubscriptionId;
    private final UUID promoCodeId;
    private final Boolean useBonus;
    private final Platform platform;
    private final Boolean isTest;
}
