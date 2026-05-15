package ru.fuelup.order.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.fuelup.common.platform.PrincipalInfo;
import ru.fuelup.order.usecase.SubscribeToOrderStatus;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderSseEndpoint {

    private static final long SSE_TIMEOUT_MS = 5 * 60 * 1000L;

    private final SubscribeToOrderStatus subscribeToOrderStatus;

    @GetMapping(value = "/{orderId}/status-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamOrderStatus(
            @PathVariable UUID orderId,
            @AuthenticationPrincipal PrincipalInfo principal
    ) {
        var emitter = new SseEmitter(SSE_TIMEOUT_MS);
        subscribeToOrderStatus.subscribe(orderId, principal.getCustomerId(), emitter);
        return emitter;
    }
}
