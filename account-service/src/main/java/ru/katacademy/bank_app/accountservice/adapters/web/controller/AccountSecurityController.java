package ru.katacademy.bank_app.accountservice.adapters.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.katacademy.securitystarter.auth.UserPrincipal;

import java.util.Map;

/**
 * Test endpoints for security-starter integration verification.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "Account Security", description = "Security integration test endpoints")
public class AccountSecurityController {

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Returns authenticated user from SecurityContext")
    public ResponseEntity<?> getCurrentUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("No authentication in SecurityContext");
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }

        if (!(authentication.getPrincipal() instanceof UserPrincipal userPrincipal)) {
            log.error("Invalid principal type: {}", authentication.getPrincipal().getClass());
            return ResponseEntity.status(401).body(Map.of("error", "Invalid principal"));
        }

        log.info("User accessed: userId={}", userPrincipal.userId());

        return ResponseEntity.ok(Map.of(
                "userId", userPrincipal.userId(),
                "authenticated", true
        ));
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Public endpoint to verify service is running")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
}