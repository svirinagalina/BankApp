package ru.katacademy.securitystarter.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.katacademy.securitystarter.auth.UserPrincipal;

/**
 * Feign interceptor для автоматической пропагации security headers.
 *
 * <p>Добавляет следующие заголовки к исходящим Feign-запросам:
 * <ul>
 *   <li><b>X-Service-Name</b> - имя текущего сервиса (из spring.application.name)</li>
 *   <li><b>X-User-Id</b> - userId из SecurityContext (если есть USER identity)</li>
 * </ul>
 *
 * <p>Это позволяет downstream-сервисам знать:
 * <ol>
 *   <li>Какой сервис вызывает их (для S2S аутентификации)</li>
 *   <li>От имени какого пользователя выполняется запрос (для user context propagation)</li>
 * </ol>
 *
 * @author Galina
 * @since 2026-02-04
 */
public class FeignSecurityInterceptor implements RequestInterceptor {

    private static final Logger log = LoggerFactory.getLogger(FeignSecurityInterceptor.class);
    private static final String SERVICE_NAME_HEADER = "X-Service-Name";
    private static final String USER_ID_HEADER = "X-User-Id";

    private final String serviceName;

    /**
     * Создает interceptor с именем текущего сервиса.
     *
     * @param serviceName имя текущего сервиса (из spring.application.name)
     */
    public FeignSecurityInterceptor(String serviceName) {
        if (serviceName == null || serviceName.isBlank()) {
            throw new IllegalArgumentException("Service name cannot be null or blank");
        }
        this.serviceName = serviceName;
    }

    @Override
    public void apply(RequestTemplate template) {
        // Always add X-Service-Name
        template.header(SERVICE_NAME_HEADER, serviceName);
        log.debug("Added {} header: {}", SERVICE_NAME_HEADER, serviceName);

        // Add X-User-Id if USER identity present in SecurityContext
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal userPrincipal) {
            if (userPrincipal.isUser()) {
                final Long userId = userPrincipal.getUserId();
                template.header(USER_ID_HEADER, String.valueOf(userId));
                log.debug("Added {} header: {}", USER_ID_HEADER, userId);
            } else {
                log.debug("Skipping {} header - principal is SERVICE, not USER", USER_ID_HEADER);
            }
        } else {
            log.debug("No UserPrincipal in SecurityContext - skipping {} header", USER_ID_HEADER);
        }
    }
}
