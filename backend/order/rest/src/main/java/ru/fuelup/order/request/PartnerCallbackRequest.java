package ru.fuelup.order.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record PartnerCallbackRequest(
        @JsonProperty("order_id") UUID partnerOrderId,
        @JsonProperty("event_type") String eventType,
        @JsonProperty("status") String status,
        @JsonProperty("actual_amount") Double actualAmount,
        @JsonProperty("actual_sum") Double actualSum,
        @JsonProperty("fail_reason") String failReason
) {}
