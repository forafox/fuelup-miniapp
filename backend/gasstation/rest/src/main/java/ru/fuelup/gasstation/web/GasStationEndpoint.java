package ru.fuelup.gasstation.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.fuelup.common.platform.PrincipalInfo;
import ru.fuelup.gasstation.response.GasStationDetailResponse;
import ru.fuelup.gasstation.response.GasStationListResponse;
import ru.fuelup.gasstation.usecase.GetGasStationById;
import ru.fuelup.gasstation.usecase.GetNearbyGasStations;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/gas-stations")
@RequiredArgsConstructor
public class GasStationEndpoint {

    private final GetNearbyGasStations getNearbyGasStations;
    private final GetGasStationById getGasStationById;

    @GetMapping
    public ResponseEntity<GasStationListResponse> getNearby(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "5000") int radiusMeters,
            @RequestParam(required = false) String brandCode,
            @RequestParam(required = false) String fuelType,
            @AuthenticationPrincipal PrincipalInfo principal
    ) {
        var result = getNearbyGasStations.invoke(
                latitude, longitude, radiusMeters, brandCode, fuelType, principal.getCustomerId()
        );
        return ResponseEntity.ok(GasStationListResponse.from(result));
    }

    @GetMapping("/{stationId}")
    public ResponseEntity<GasStationDetailResponse> getById(
            @PathVariable UUID stationId,
            @AuthenticationPrincipal PrincipalInfo principal
    ) {
        return getGasStationById.invoke(stationId, principal.getCustomerId())
                .map(station -> ResponseEntity.ok(GasStationDetailResponse.from(station)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
