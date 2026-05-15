package ru.fuelup.order.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.fuelup.order.usecase.GetRecentOrders;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/internal/orders")
@RequiredArgsConstructor
public class InternalOrderEndpoint {

    private final GetRecentOrders getRecentOrders;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getOrders(
            @RequestParam Long messengerId,
            @RequestParam String platform,
            @RequestParam(defaultValue = "5") int limit
    ) {
        var orders = getRecentOrders.invoke(messengerId, platform, limit);
        return ResponseEntity.ok(orders);
    }
}
