package ru.fuelup.common.platform;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class PrincipalInfo {
    private final UUID customerId;
    private final String role;
    private final Platform platform;
}
