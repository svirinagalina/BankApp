package ru.katacademy.securitystarter.identity;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Реализация UserIdentityResolver для извлечения userId из HTTP-заголовка.
 *
 * Читает значение из заголовка X-User-Id и преобразует его в Long.
 * Если заголовок отсутствует или невалиден, возвращает null.
 *
 * @author Galina
 * @date 2026-01-23
 */
@Slf4j
@Component
public class HeaderUserIdentityResolver implements UserIdentityResolver {

    private static final String USER_ID_HEADER = "X-User-Id";

    @Override
    public Long resolve(HttpServletRequest request) {
        final String headerValue = request.getHeader(USER_ID_HEADER);

        if (headerValue == null || headerValue.isEmpty()) {
            return null;
        }

        try {
            return Long.parseLong(headerValue);
        } catch (NumberFormatException e) {
            log.warn("Invalid userId format in header {}: {}", USER_ID_HEADER, headerValue);
            return null;
        }
    }
}