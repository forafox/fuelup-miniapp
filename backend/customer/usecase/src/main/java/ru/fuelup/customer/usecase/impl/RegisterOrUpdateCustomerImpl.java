package ru.fuelup.customer.usecase.impl;

import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.fuelup.common.platform.Platform;
import ru.fuelup.customer.domain.Customer;
import ru.fuelup.customer.usecase.RegisterOrUpdateCustomer;
import ru.fuelup.customer.usecase.port.CustomerRepository;
import ru.fuelup.customer.usecase.port.InitDataValidator;
import ru.fuelup.customer.usecase.port.JwtTokenService;
import ru.fuelup.customer.usecase.result.AuthResult;

@Slf4j
@RequiredArgsConstructor
public class RegisterOrUpdateCustomerImpl implements RegisterOrUpdateCustomer {

    private final InitDataValidator initDataValidator;
    private final CustomerRepository customerRepository;
    private final JwtTokenService jwtTokenService;

    @Override
    public Either<RegisterError, AuthResult> invoke(String initData, Platform platform) {
        var validationResult = initDataValidator.validate(initData, platform);
        if (validationResult.isLeft()) {
            log.warn("initData validation failed for platform={}", platform);
            return Either.left(new RegisterError.InvalidInitDataError());
        }

        var userData = validationResult.get();

        try {
            var customer = customerRepository.findByMessengerUserId(userData.getUserId(), platform)
                    .map(existing -> updateCustomerInfo(existing, userData))
                    .orElseGet(() -> registerNewCustomer(userData, platform));

            var token = jwtTokenService.generate(customer.getId(), "CUSTOMER", platform);
            return Either.right(new AuthResult(token, customer));
        } catch (Exception e) {
            log.error("Failed to register/update customer userId={} platform={}", userData.getUserId(), platform, e);
            return Either.left(new RegisterError.CustomerPersistenceError());
        }
    }

    private Customer updateCustomerInfo(Customer existing, InitDataValidator.UserData userData) {
        existing.setFirstName(userData.getFirstName());
        existing.setLastName(userData.getLastName());
        existing.setUsername(userData.getUsername());
        existing.setUpdatedAt(System.currentTimeMillis());
        return customerRepository.save(existing);
    }

    private Customer registerNewCustomer(InitDataValidator.UserData userData, Platform platform) {
        var customer = Customer.newFromMessenger(
                userData.getUserId(), platform,
                userData.getFirstName(), userData.getLastName(), userData.getUsername()
        );
        return customerRepository.save(customer);
    }
}
