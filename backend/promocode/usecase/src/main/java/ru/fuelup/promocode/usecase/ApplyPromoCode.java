package ru.fuelup.promocode.usecase;

import io.vavr.control.Either;
import ru.fuelup.common.exceptions.BusinessError;
import ru.fuelup.promocode.usecase.result.PromoCodeResult;

import java.util.UUID;

public interface ApplyPromoCode {

    Either<ApplyError, PromoCodeResult> invoke(String code, UUID customerId, UUID gasStationId, String fuelType);

    sealed class ApplyError implements BusinessError {
        private ApplyError() {}

        public static final class CodeNotFound extends ApplyError {}

        public static final class CodeExpired extends ApplyError {}

        public static final class CodeExhausted extends ApplyError {}

        public static final class NotApplicableToStation extends ApplyError {
            private final String reason;
            public NotApplicableToStation(String reason) { this.reason = reason; }
            public String getReason() { return reason; }
        }

        public static final class AlreadyUsedByCustomer extends ApplyError {}
    }
}
