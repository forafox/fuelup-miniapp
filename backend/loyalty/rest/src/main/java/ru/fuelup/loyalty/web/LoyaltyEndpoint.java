package ru.fuelup.loyalty.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.fuelup.common.platform.PrincipalInfo;
import ru.fuelup.loyalty.usecase.GetBonusBalance;
import ru.fuelup.loyalty.usecase.GetBonusHistory;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/loyalty")
@RequiredArgsConstructor
public class LoyaltyEndpoint {

    private final GetBonusBalance getBonusBalance;
    private final GetBonusHistory getBonusHistory;

    @GetMapping("/balance")
    public ResponseEntity<Map<String, Long>> getBalance(@AuthenticationPrincipal PrincipalInfo principal) {
        var balance = getBonusBalance.invoke(principal.getCustomerId());
        return balance
                .map(b -> ResponseEntity.ok(Map.of("balance", b.getBalance(), "pendingBalance", b.getPendingBalance())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/history")
    public ResponseEntity<?> getHistory(
            @AuthenticationPrincipal PrincipalInfo principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var history = getBonusHistory.invoke(principal.getCustomerId(), page, size);
        return ResponseEntity.ok(history);
    }
}
