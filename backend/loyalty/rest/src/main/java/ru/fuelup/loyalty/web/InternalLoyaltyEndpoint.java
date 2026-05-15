package ru.fuelup.loyalty.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.fuelup.loyalty.usecase.GetBonusBalance;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/internal/loyalty")
@RequiredArgsConstructor
public class InternalLoyaltyEndpoint {

    private final GetBonusBalance getBonusBalance;

    @GetMapping("/balance")
    public ResponseEntity<Map<String, Long>> getBalance(
            @RequestParam UUID customerId
    ) {
        return getBonusBalance.invoke(customerId)
                .map(b -> ResponseEntity.ok(Map.of(
                        "balance", b.getBalance(),
                        "pendingBalance", b.getPendingBalance()
                )))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
