package ru.katacademy.securitystarter.identity;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Composite resolver для последовательной проверки нескольких UserIdentityResolver.
 *
 * Проходит по цепочке резолверов и возвращает первую найденную идентичность.
 * Это позволяет реализовать приоритет: например, сначала проверить X-Service-Name,
 * затем X-User-Id, затем JWT.
 *
 * @author Galina
 * @since 2026-02-04
 */
public class CompositeUserIdentityResolver implements UserIdentityResolver {

    private static final Logger log = LoggerFactory.getLogger(CompositeUserIdentityResolver.class);

    private final List<UserIdentityResolver> resolvers;

    /**
     * Создает composite resolver с заданным списком резолверов.
     * Резолверы вызываются в порядке, указанном в списке.
     *
     * @param resolvers список резолверов (порядок определяет приоритет)
     */
    public CompositeUserIdentityResolver(List<UserIdentityResolver> resolvers) {
        if (resolvers == null || resolvers.isEmpty()) {
            throw new IllegalArgumentException("Resolvers list cannot be null or empty");
        }
        this.resolvers = List.copyOf(resolvers);
    }

    @Override
    public UserIdentity resolve(HttpServletRequest request) {
        for (UserIdentityResolver resolver : resolvers) {
            final UserIdentity identity = resolver.resolve(request);
            if (identity != null) {
                log.debug("Identity resolved by {}: {}",
                    resolver.getClass().getSimpleName(),
                    identity.getDisplayName());
                return identity;
            }
        }

        log.debug("No identity resolved by any resolver");
        return null;
    }
}
