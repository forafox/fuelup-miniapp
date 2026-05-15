package ru.fuelup.order.usecase.port;

import io.vavr.control.Either;
import lombok.Value;
import ru.fuelup.common.order.OrderPaymentType;

import java.util.UUID;

public interface PaymentPort {

    Either<String, PaymentResult> createPayment(
            UUID orderId,
            Double amount,
            OrderPaymentType paymentType,
            UUID sbpSubscriptionId
    );

    @Value
    class PaymentResult {
        String paymentUrl;
        String externalPaymentId;
    }
}
