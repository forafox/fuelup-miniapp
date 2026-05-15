package ru.fuelup.order.web;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.fuelup.order.usecase.GetOrdersForCrm;

@RestController
@RequestMapping("/api/v1/crm")
@RequiredArgsConstructor
public class CrmOrderEndpoint {

    private final GetOrdersForCrm getOrdersForCrm;

    @GetMapping("/orders")
    public ResponseEntity<Page<GetOrdersForCrm.CrmOrderDto>> getOrders(
            @RequestParam(required = false) String status,
            @PageableDefault(size = 50) Pageable pageable
    ) {
        return ResponseEntity.ok(getOrdersForCrm.invoke(status, pageable));
    }
}
