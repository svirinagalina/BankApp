package ru.katacademy.securitystarter.identity;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.katacademy.securitystarter.jwt.JwtParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Реализация UserIdentityResolver для извлечения userId из JWT токена.
 *
 * <p>Приоритет разрешения:
 * <ol>
 *   <li>JWT из заголовка Authorization: Bearer</li>
 *   <li>Fallback на X-User-Id header (если JWT отсутствует)</li>
 * </ol>
 *
 * <p>JWT claims извлекаются в UserIdentity.attributes:
 * <ul>
 *   <li><b>sub</b> - userId (обязательный)</li>
 *   <li><b>roles</b> - список ролей</li>
 *   <li>Все остальные claims сохраняются как attributes</li>
 * </ul>
 *
 * @author Galina
 * @since 2026-02-04
 */
public class JwtUserIdentityResolver implements UserIdentityResolver {

    private static final Logger log = LoggerFactory.getLogger(JwtUserIdentityResolver.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String USER_ID_HEADER = "X-User-Id";

    private final JwtParser jwtParser;
    private final HeaderUserIdentityResolver fallbackResolver;

    /**
     * Создает resolver с JWT parser и fallback на X-User-Id.
     *
     * @param jwtParser парсер JWT токенов
     */
    public JwtUserIdentityResolver(JwtParser jwtParser) {
        this.jwtParser = jwtParser;
        this.fallbackResolver = new HeaderUserIdentityResolver();
    }

    @Override
    public UserIdentity resolve(HttpServletRequest request) {
        final String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);

        // Try JWT first
        if (authorizationHeader != null) {
            final Map<String, Object> claims = jwtParser.parseToken(authorizationHeader);
            if (claims != null) {
                return extractUserIdentityFromClaims(claims);
            }
        }

        // Fallback to X-User-Id header
        log.debug("No valid JWT found, falling back to X-User-Id header");
        return fallbackResolver.resolve(request);
    }

    /**
     * Извлекает UserIdentity из JWT claims.
     *
     * @param claims JWT claims
     * @return UserIdentity с userId, roles, и attributes
     */
    private UserIdentity extractUserIdentityFromClaims(Map<String, Object> claims) {
        // Extract userId from 'sub' claim
        final Object subClaim = claims.get("sub");
        if (subClaim == null) {
            log.warn("JWT missing 'sub' claim - cannot extract userId");
            return null;
        }

        final Long userId;
        try {
            if (subClaim instanceof Number) {
                userId = ((Number) subClaim).longValue();
            } else {
                userId = Long.parseLong(subClaim.toString());
            }
        } catch (NumberFormatException e) {
            log.warn("Invalid userId in JWT 'sub' claim: {}", subClaim);
            return null;
        }

        // Extract roles from 'roles' claim (optional)
        final List<String> roles = extractRoles(claims);

        log.debug("Resolved userId from JWT: userId={}, roles={}", userId, roles);
        return UserIdentity.userWithAttributes(userId, roles, claims);
    }

    /**
     * Извлекает список ролей из JWT claims.
     *
     * @param claims JWT claims
     * @return список ролей или пустой список
     */
    @SuppressWarnings("unchecked")
    private List<String> extractRoles(Map<String, Object> claims) {
        final Object rolesClaim = claims.get("roles");
        if (rolesClaim == null) {
            return List.of();
        }

        if (rolesClaim instanceof List) {
            try {
                return new ArrayList<>((List<String>) rolesClaim);
            } catch (ClassCastException e) {
                log.warn("Invalid roles claim format - expected List<String>");
                return List.of();
            }
        }

        if (rolesClaim instanceof String) {
            return List.of((String) rolesClaim);
        }

        log.warn("Unknown roles claim type: {}", rolesClaim.getClass());
        return List.of();
    }
}
