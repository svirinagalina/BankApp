package ru.katacademy.kycservice.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.katacademy.securitystarter.identity.CompositeUserIdentityResolver;
import ru.katacademy.securitystarter.identity.HeaderUserIdentityResolver;
import ru.katacademy.securitystarter.identity.ServiceIdentityResolver;
import ru.katacademy.securitystarter.identity.UserIdentity;
import ru.katacademy.securitystarter.identity.UserIdentityResolver;

import java.util.List;

/**
 * KYC-service implementation of UserIdentityResolver contract.
 *
 * <p>Поддерживает два типа аутентификации:
 * <ul>
 *   <li><b>SERVICE</b> - через X-Service-Name (для вызовов от других сервисов)</li>
 *   <li><b>USER</b> - через X-User-Id (для прямых вызовов от пользователей)</li>
 * </ul>
 *
 * <p>Приоритет: SERVICE > USER (если оба заголовка присутствуют, используется SERVICE).
 *
 * @author Galina
 * @since 2026-02-04
 */
@Slf4j
@Component
public class KycUserIdentityResolver implements UserIdentityResolver {

    private final CompositeUserIdentityResolver compositeResolver;

    public KycUserIdentityResolver() {
        this.compositeResolver = new CompositeUserIdentityResolver(
            List.of(
                new ServiceIdentityResolver(),  // Priority 1: X-Service-Name
                new HeaderUserIdentityResolver() // Priority 2: X-User-Id
            )
        );
    }

    @Override
    public UserIdentity resolve(HttpServletRequest request) {
        final UserIdentity identity = compositeResolver.resolve(request);

        if (identity != null) {
            if (identity.isService()) {
                log.debug("Resolved SERVICE identity: serviceName={}", identity.serviceName());
            } else {
                log.debug("Resolved USER identity: userId={}", identity.userId());
            }
        } else {
            log.debug("No identity resolved from request. URI: {}", request.getRequestURI());
        }

        return identity;
    }
}
