package ru.fuelup.order.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.fuelup.order.request.PartnerCallbackRequest;
import ru.fuelup.order.usecase.ProcessPartnerCallback;

@Slf4j
@RestController
@RequestMapping("/api/v1/partner/callback")
@RequiredArgsConstructor
public class PartnerCallbackEndpoint {

    private final ProcessPartnerCallback processPartnerCallback;

    @PostMapping("/fueling")
    public ResponseEntity<Void> fuelingCallback(@RequestBody PartnerCallbackRequest request) {
        log.info("Partner callback received: partnerOrderId={} event={} status={}",
                request.partnerOrderId(), request.eventType(), request.status());

        processPartnerCallback.invoke(new ProcessPartnerCallback.CallbackCommand(
                request.partnerOrderId(),
                request.eventType(),
                request.status(),
                request.actualAmount(),
                request.actualSum(),
                request.failReason()
        ));

        return ResponseEntity.ok().build();
    }
}
