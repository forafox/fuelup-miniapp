package ru.fuelup.customer.usecase;

import io.vavr.control.Either;
import ru.fuelup.common.exceptions.BusinessError;
import ru.fuelup.common.platform.Platform;
import ru.fuelup.customer.usecase.result.AuthResult;

public interface RegisterOrUpdateCustomer {

    Either<RegisterError, AuthResult> invoke(String initData, Platform platform);

    sealed class RegisterError implements BusinessError {
        private RegisterError() {}

        public static final class InvalidInitDataError extends RegisterError {}

        public static final class CustomerPersistenceError extends RegisterError {}
    }
}
