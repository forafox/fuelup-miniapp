package ru.fuelup.order.usecase;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetOrdersForCrm {

    Page<CrmOrderDto> invoke(String statusFilter, Pageable pageable);

    record CrmOrderDto(
            String orderId,
            String customerId,
            String gasStationName,
            String fuelType,
            Double requestedAmount,
            Double actualAmount,
            Double requestedSum,
            String status,
            Long createdAt,
            String platform
    ) {}
}
