package ru.fuelup.customer.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.fuelup.common.platform.Platform;

import java.util.UUID;

@Data
@AllArgsConstructor
public class Customer {
    private UUID id;
    private Long messengerUserId;
    private Platform platform;
    private String firstName;
    private String lastName;
    private String username;
    private String phoneNumber;
    private OnboardingStatus onboardingStatus;
    private String preferredFuelType;
    private Double tankVolume;
    private Long bonusBalance;
    private String referralSource;
    private Long createdAt;
    private Long updatedAt;

    /** Новый клиент, только что зарегистрировавшийся через initData */
    public static Customer newFromMessenger(Long messengerUserId, Platform platform,
                                            String firstName, String lastName, String username) {
        return new Customer(
                null,
                messengerUserId,
                platform,
                firstName,
                lastName,
                username,
                null,
                OnboardingStatus.NOT_STARTED,
                null,
                null,
                0L,
                null,
                System.currentTimeMillis(),
                System.currentTimeMillis()
        );
    }

    public boolean hasCompletedOnboarding() {
        return onboardingStatus == OnboardingStatus.COMPLETED;
    }
}
