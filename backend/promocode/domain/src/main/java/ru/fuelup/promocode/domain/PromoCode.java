package ru.fuelup.promocode.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
public class PromoCode {
    private UUID id;
    private UUID campaignId;
    private String code;
    private PromoCodeStatus status;
    private Instant validFrom;
    private Instant validUntil;
    private Integer usageLimit;
    private Integer usageCount;
    private String targetBrandCode;
    private String targetFuelGroup;

    public boolean isValid() {
        var now = Instant.now();
        return status == PromoCodeStatus.ACTIVE
                && now.isAfter(validFrom)
                && now.isBefore(validUntil)
                && (usageLimit == null || usageCount < usageLimit);
    }

    public boolean isApplicableTo(String brandCode, String fuelType) {
        boolean brandMatch = targetBrandCode == null || targetBrandCode.equals(brandCode);
        boolean fuelMatch = targetFuelGroup == null || fuelType.startsWith(targetFuelGroup);
        return brandMatch && fuelMatch;
    }

    public enum PromoCodeStatus {
        ACTIVE,
        EXHAUSTED,
        EXPIRED,
        DISABLED,
    }
}
