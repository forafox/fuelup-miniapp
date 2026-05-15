package ru.fuelup.loyalty.usecase;

import io.vavr.control.Either;
import ru.fuelup.common.exceptions.BusinessError;

import java.util.UUID;

public interface AccrueBonus {

    Either<AccrueBonusError, Long> invoke(UUID customerId, UUID orderId, double orderSum);

    sealed class AccrueBonusError implements BusinessError {
        private AccrueBonusError() {}
        public static final class CustomerNotFound extends AccrueBonusError {}
        public static final class AlreadyAccrued extends AccrueBonusError {}
    }
}
