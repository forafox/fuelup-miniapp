package ru.fuelup.order.usecase;

import java.util.UUID;

public interface ProcessPartnerCallback {

    void invoke(CallbackCommand command);

    record CallbackCommand(
            UUID partnerOrderId,
            String eventType,
            String newStatus,
            Double actualAmount,
            Double actualSum,
            String failReason
    ) {}
}
