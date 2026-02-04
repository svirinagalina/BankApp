package ru.katacademy.securitystarter.identity;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Реализация UserIdentityResolver для извлечения имени сервиса из HTTP-заголовка.
 *
 * Читает значение из заголовка X-Service-Name и создает UserIdentity типа SERVICE.
 * Если заголовок отсутствует или пустой, возвращает null.
 *
 * @author Galina
 * @since 2026-02-04
 */
public class ServiceIdentityResolver implements UserIdentityResolver {

    private static final Logger log = LoggerFactory.getLogger(ServiceIdentityResolver.class);
    private static final String SERVICE_NAME_HEADER = "X-Service-Name";

    @Override
    public UserIdentity resolve(HttpServletRequest request) {
        final String headerValue = request.getHeader(SERVICE_NAME_HEADER);

        if (headerValue == null || headerValue.isBlank()) {
            return null;
        }

        log.debug("Resolved service identity: serviceName={}", headerValue);
        return UserIdentity.service(headerValue);
    }
}
