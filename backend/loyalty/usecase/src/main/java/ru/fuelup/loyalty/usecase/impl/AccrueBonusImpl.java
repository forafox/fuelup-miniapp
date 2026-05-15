package ru.fuelup.loyalty.usecase.impl;

import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.fuelup.loyalty.domain.BonusTransaction;
import ru.fuelup.loyalty.usecase.AccrueBonus;
import ru.fuelup.loyalty.usecase.port.BonusAccountRepository;
import ru.fuelup.loyalty.usecase.port.BonusConfigPort;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class AccrueBonusImpl implements AccrueBonus {

    // 1 бонус = 1 рубль при списании. Начисляется процент от суммы заказа.
    private static final double DEFAULT_ACCRUAL_RATE = 0.01; // 1%

    private final BonusAccountRepository accountRepo;
    private final BonusConfigPort configPort;

    @Override
    public Either<AccrueBonusError, Long> invoke(UUID customerId, UUID orderId, double orderSum) {
        var account = accountRepo.findByCustomerId(customerId);
        if (account.isEmpty()) {
            return Either.left(new AccrueBonusError.CustomerNotFound());
        }

        if (accountRepo.hasTransactionForOrder(orderId)) {
            log.warn("Bonus already accrued for orderId={}", orderId);
            return Either.left(new AccrueBonusError.AlreadyAccrued());
        }

        double rate = configPort.getAccrualRate(customerId).orElse(DEFAULT_ACCRUAL_RATE);
        long bonusAmount = Math.max(1L, Math.round(orderSum * rate));

        var tx = BonusTransaction.accrual(customerId, orderId, bonusAmount);
        accountRepo.addTransaction(tx);
        accountRepo.incrementBalance(customerId, bonusAmount);

        log.info("Accrued {} bonuses to customerId={} for orderId={}", bonusAmount, customerId, orderId);
        return Either.right(bonusAmount);
    }
}
