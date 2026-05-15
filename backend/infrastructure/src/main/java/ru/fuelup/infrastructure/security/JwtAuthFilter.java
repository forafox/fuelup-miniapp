package ru.fuelup.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.fuelup.common.platform.Platform;
import ru.fuelup.common.platform.PrincipalInfo;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String INTERNAL_HEADER = "X-Internal-Api-Key";

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        var internalKey = request.getHeader(INTERNAL_HEADER);
        if (internalKey != null) {
            handleInternalRequest(internalKey, request);
            chain.doFilter(request, response);
            return;
        }

        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        var token = authHeader.substring(BEARER_PREFIX.length());
        try {
            var claims = jwtService.parse(token);
            var principal = new PrincipalInfo(
                    UUID.fromString(claims.getSubject()),
                    claims.get("role", String.class),
                    Platform.valueOf(claims.get("platform", String.class))
            );
            var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + principal.getRole()));
            var auth = new UsernamePasswordAuthenticationToken(principal, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception e) {
            log.debug("JWT validation failed: {}", e.getMessage());
        }

        chain.doFilter(request, response);
    }

    private void handleInternalRequest(String key, HttpServletRequest request) {
        if (jwtService.isValidInternalKey(key)) {
            var principal = new PrincipalInfo(null, "INTERNAL", null);
            var auth = new UsernamePasswordAuthenticationToken(
                    principal, null, List.of(new SimpleGrantedAuthority("ROLE_INTERNAL"))
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
    }
}
