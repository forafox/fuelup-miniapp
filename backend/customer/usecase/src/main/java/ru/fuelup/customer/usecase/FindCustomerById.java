package ru.fuelup.customer.usecase;

import ru.fuelup.customer.domain.Customer;

import java.util.Optional;
import java.util.UUID;

public interface FindCustomerById {
    Optional<Customer> invoke(UUID customerId);
}
