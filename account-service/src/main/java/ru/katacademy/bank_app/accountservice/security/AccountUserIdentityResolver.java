package ru.katacademy.bank_app.accountservice.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.katacademy.securitystarter.identity.UserIdentityResolver;

/**
 * Account-service implementation of UserIdentityResolver contract.
 * <p>
 * Responsibilities:
 * - Extract userId from HTTP request (MVP: using X-User-Id header)
 * - Return userId as Long, or null if not present/invalid
 * <p>
 * UserPrincipal creation and SecurityContext population is handled by security-starter.
 * <p>
 * This resolver can be replaced with JWT parsing, session-based auth, OAuth2, etc.
 * without changing security-starter.
 */
@Slf4j
@Component
public class AccountUserIdentityResolver implements UserIdentityResolver {

    private static final String USER_ID_HEADER = "X-User-Id";

    /**
     * Extracts user ID from X-User-Id header.
     * <p>
     * MVP implementation: reads userId from HTTP header.
     * In production, this could extract from JWT token, session, OAuth2 token, etc.
     *
     * @param request HTTP request
     * @return userId as Long, or null if header is missing or invalid
     */
    @Override
    public Long resolve(HttpServletRequest request) {
        final String userIdHeader = request.getHeader(USER_ID_HEADER);

        if (userIdHeader == null || userIdHeader.isBlank()) {
            log.debug("X-User-Id header missing. URI: {}", request.getRequestURI());
            return null;
        }

        try {
            final Long userId = Long.parseLong(userIdHeader);
            log.debug("Resolved userId: {} from header. URI: {}", userId, request.getRequestURI());
            return userId;
        } catch (NumberFormatException e) {
            log.warn("Invalid X-User-Id header value: '{}'. URI: {}. Expected numeric value.",
                    userIdHeader, request.getRequestURI());
            return null;
        }
    }
}