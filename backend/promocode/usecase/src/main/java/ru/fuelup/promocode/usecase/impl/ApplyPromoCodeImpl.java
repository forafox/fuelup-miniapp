package ru.fuelup.promocode.usecase.impl;

import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.fuelup.promocode.domain.PromoCode;
import ru.fuelup.promocode.usecase.ApplyPromoCode;
import ru.fuelup.promocode.usecase.port.GasStationBrandPort;
import ru.fuelup.promocode.usecase.port.PromoCodeRepository;
import ru.fuelup.promocode.usecase.result.PromoCodeResult;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class ApplyPromoCodeImpl implements ApplyPromoCode {

    private final PromoCodeRepository promoCodeRepo;
    private final GasStationBrandPort brandPort;

    @Override
    public Either<ApplyError, PromoCodeResult> invoke(
            String code, UUID customerId, UUID gasStationId, String fuelType
    ) {
        var promoCode = promoCodeRepo.findByCode(code.trim().toUpperCase());
        if (promoCode.isEmpty()) {
            return Either.left(new ApplyError.CodeNotFound());
        }

        var promo = promoCode.get();

        if (!promo.isValid()) {
            return switch (promo.getStatus()) {
                case EXHAUSTED -> Either.left(new ApplyError.CodeExhausted());
                default        -> Either.left(new ApplyError.CodeExpired());
            };
        }

        if (promoCodeRepo.hasCustomerUsedCode(customerId, promo.getId())) {
            return Either.left(new ApplyError.AlreadyUsedByCustomer());
        }

        var brandCode = brandPort.getBrandCodeByStationId(gasStationId).orElse(null);
        if (!promo.isApplicableTo(brandCode, fuelType)) {
            String reason = brandCode != null
                    ? "Промокод не действует на АЗС бренда " + brandCode
                    : "Промокод не применим к данному топливу";
            return Either.left(new ApplyError.NotApplicableToStation(reason));
        }

        log.info("PromoCode {} applied: customer={} station={}", code, customerId, gasStationId);
        return Either.right(PromoCodeResult.from(promo));
    }
}
