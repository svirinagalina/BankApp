package ru.katacademy.securitystarter.identity;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Реализация UserIdentityResolver для извлечения userId из HTTP-заголовка.
 *
 * Читает значение из заголовка X-User-Id и преобразует его в UserIdentity.
 * Если заголовок отсутствует или невалиден, возвращает null.
 *
 * @author Galina
 * @since 2026-01-23
 */
public class HeaderUserIdentityResolver implements UserIdentityResolver {

    private static final Logger log = LoggerFactory.getLogger(HeaderUserIdentityResolver.class);
    private static final String USER_ID_HEADER = "X-User-Id";

    @Override
    public UserIdentity resolve(HttpServletRequest request) {
        final String headerValue = request.getHeader(USER_ID_HEADER);

        if (headerValue == null || headerValue.isEmpty()) {
            return null;
        }

        try {
            final Long userId = Long.parseLong(headerValue);
            return new UserIdentity(userId);
        } catch (NumberFormatException e) {
            log.warn("Invalid userId format in header {}: {}", USER_ID_HEADER, headerValue);
            return null;
        }
    }
}