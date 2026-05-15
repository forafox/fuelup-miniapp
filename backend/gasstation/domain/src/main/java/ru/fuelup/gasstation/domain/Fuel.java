package ru.fuelup.gasstation.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Fuel {
    private String type;
    private String localizedName;
    private Double basePrice;
    /** Цена с учётом скидки по программе лояльности бренда */
    private Double discountedPrice;
    /** Итоговая цена для клиента с учётом персональных промокодов и бустеров */
    private Double clientPrice;

    public boolean hasDiscount() {
        return discountedPrice != null && discountedPrice < basePrice;
    }
}
