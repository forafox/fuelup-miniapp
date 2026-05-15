package ru.fuelup.loyalty.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class BonusTransaction {
    private UUID id;
    private UUID customerId;
    private UUID orderId;
    private TransactionType type;
    private Long amount;
    private String description;
    private Long timestamp;

    public enum TransactionType {
        ACCRUAL,
        WITHDRAWAL,
        WELCOME_BONUS,
        REFERRAL_BONUS,
        EXPIRATION,
    }

    public static BonusTransaction accrual(UUID customerId, UUID orderId, long amount) {
        return new BonusTransaction(
                null, customerId, orderId,
                TransactionType.ACCRUAL, amount,
                "Начисление за заправку",
                System.currentTimeMillis()
        );
    }

    public static BonusTransaction welcome(UUID customerId) {
        return new BonusTransaction(
                null, customerId, null,
                TransactionType.WELCOME_BONUS, 100L,
                "Приветственный бонус",
                System.currentTimeMillis()
        );
    }
}
