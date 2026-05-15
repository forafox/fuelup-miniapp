package ru.fuelup.order.usecase;

import io.vavr.control.Either;
import ru.fuelup.common.exceptions.BusinessError;
import ru.fuelup.order.usecase.command.OrderCommand;
import ru.fuelup.order.usecase.result.OrderResult;

public interface CreateOrder {

    Either<CreateOrderError, OrderResult> invoke(OrderCommand command);

    sealed class CreateOrderError implements BusinessError {
        private CreateOrderError() {}

        public static final class CustomerNotFoundError extends CreateOrderError {}

        public static final class GasStationNotFoundError extends CreateOrderError {}

        public static final class PriceMismatchError extends CreateOrderError {
            private final double expected;
            private final double actual;

            public PriceMismatchError(double expected, double actual) {
                this.expected = expected;
                this.actual = actual;
            }

            public double getExpected() { return expected; }
            public double getActual() { return actual; }
        }

        public static final class PartnerApiUnavailableError extends CreateOrderError {}

        public static final class PaymentCreationError extends CreateOrderError {}

        public static final class DailyLimitExceededError extends CreateOrderError {}
    }
}
