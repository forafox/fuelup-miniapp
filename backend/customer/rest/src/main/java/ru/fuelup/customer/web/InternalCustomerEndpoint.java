package ru.fuelup.customer.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.fuelup.common.platform.Platform;
import ru.fuelup.customer.domain.Customer;
import ru.fuelup.customer.usecase.FindCustomerById;
import ru.fuelup.customer.usecase.RegisterOrUpdateCustomer;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/internal/customers")
@RequiredArgsConstructor
public class InternalCustomerEndpoint {

    private final RegisterOrUpdateCustomer registerOrUpdateCustomer;
    private final FindCustomerById findCustomerById;

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerDto> getById(@PathVariable UUID customerId) {
        return findCustomerById.invoke(customerId)
                .map(c -> ResponseEntity.ok(CustomerDto.from(c)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/register")
    public ResponseEntity<RegisteredCustomerDto> register(@RequestBody RegisterRequest request) {
        var platform = Platform.valueOf(request.platform());
        var initData = buildSyntheticInitData(request);
        return registerOrUpdateCustomer.invoke(initData, platform)
                .fold(
                        err -> ResponseEntity.badRequest().build(),
                        result -> ResponseEntity.ok(RegisteredCustomerDto.from(result.getCustomer()))
                );
    }

    private String buildSyntheticInitData(RegisterRequest request) {
        return "internal_register|" + request.messengerUserId() + "|" + request.firstName();
    }

    record RegisterRequest(
            Long messengerUserId,
            String firstName,
            String lastName,
            String username,
            String platform
    ) {}

    record CustomerDto(String id, Long messengerUserId, String firstName, String platform) {
        static CustomerDto from(Customer c) {
            return new CustomerDto(
                    c.getId().toString(),
                    c.getMessengerUserId(),
                    c.getFirstName(),
                    c.getPlatform().name()
            );
        }
    }

    record RegisteredCustomerDto(String id, String name, Long bonusBalance, String onboardingStatus) {
        static RegisteredCustomerDto from(Customer c) {
            return new RegisteredCustomerDto(
                    c.getId().toString(),
                    c.getFirstName(),
                    c.getBonusBalance(),
                    c.getOnboardingStatus().name()
            );
        }
    }
}
