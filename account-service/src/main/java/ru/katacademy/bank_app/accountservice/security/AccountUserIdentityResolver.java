package ru.katacademy.bank_app.accountservice.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.katacademy.securitystarter.identity.UserIdentity;
import ru.katacademy.securitystarter.identity.UserIdentityResolver;

@Slf4j
@Component
public class AccountUserIdentityResolver implements UserIdentityResolver {

    private static final String USER_ID_HEADER = "X-User-Id";

    @Override
    public UserIdentity resolve(HttpServletRequest request) {
        final String userIdHeader = request.getHeader(USER_ID_HEADER);

        if (userIdHeader == null || userIdHeader.isBlank()) {
            log.debug("X-User-Id header missing. URI: {}", request.getRequestURI());
            return null;
        }

        try {
            final Long userId = Long.parseLong(userIdHeader);
            log.debug("Resolved userId: {} from header. URI: {}", userId, request.getRequestURI());
            return new UserIdentity(userId);
        } catch (NumberFormatException e) {
            log.warn("Invalid X-User-Id header value: '{}'. URI: {}",
                    userIdHeader, request.getRequestURI());
            return null;
        }
    }
}