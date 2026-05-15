package ru.fuelup.loyalty.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class BonusAccount {
    private UUID id;
    private UUID customerId;
    private Long balance;
    private Long pendingBalance;
    private Long totalEarned;
    private Long totalSpent;
    private Long updatedAt;

    public boolean hasSufficientBalance(long required) {
        return balance >= required;
    }

    public BonusAccount debit(long amount) {
        if (!hasSufficientBalance(amount)) {
            throw new IllegalStateException(
                    "Insufficient bonus balance: required=%d, available=%d".formatted(amount, balance)
            );
        }
        this.balance -= amount;
        this.totalSpent += amount;
        this.updatedAt = System.currentTimeMillis();
        return this;
    }

    public BonusAccount credit(long amount) {
        this.balance += amount;
        this.totalEarned += amount;
        this.updatedAt = System.currentTimeMillis();
        return this;
    }
}
