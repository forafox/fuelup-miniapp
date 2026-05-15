package ru.fuelup.customer.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.fuelup.customer.request.AuthRequest;
import ru.fuelup.customer.response.AuthResponse;
import ru.fuelup.customer.usecase.RegisterOrUpdateCustomer;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthEndpoint {

    private final RegisterOrUpdateCustomer registerOrUpdateCustomer;

    @PostMapping("/messenger")
    public ResponseEntity<AuthResponse> authenticateViaMessenger(@RequestBody AuthRequest request) {
        return registerOrUpdateCustomer.invoke(request.getInitData(), request.getPlatform())
                .fold(
                        error -> switch (error) {
                            case RegisterOrUpdateCustomer.RegisterError.InvalidInitDataError e ->
                                    ResponseEntity.status(401).body(AuthResponse.unauthorized());
                            default -> ResponseEntity.internalServerError().build();
                        },
                        result -> ResponseEntity.ok(AuthResponse.success(
                                result.getJwtToken(),
                                result.getCustomer()
                        ))
                );
    }
}
