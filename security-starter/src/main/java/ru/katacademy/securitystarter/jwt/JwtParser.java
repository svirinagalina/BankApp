package ru.katacademy.securitystarter.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Парсер JWT токенов с поддержкой валидации подписи.
 *
 * <p>Может работать в двух режимах:
 * <ul>
 *   <li><b>С валидацией подписи</b> - полная проверка токена (для критических сервисов)</li>
 *   <li><b>Без валидации подписи</b> - только парсинг claims (если Gateway уже проверил)</li>
 * </ul>
 *
 * @author Galina
 * @since 2026-02-04
 */
public class JwtParser {

    private static final Logger log = LoggerFactory.getLogger(JwtParser.class);
    private static final String BEARER_PREFIX = "Bearer ";

    private final SecretKey secretKey;
    private final boolean validateSignature;

    /**
     * Создает JWT parser с заданными параметрами.
     *
     * @param secret секретный ключ (минимум 32 символа)
     * @param validateSignature включить валидацию подписи
     */
    public JwtParser(String secret, boolean validateSignature) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 characters");
        }
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.validateSignature = validateSignature;
    }

    /**
     * Парсит JWT токен из Bearer header и извлекает claims.
     *
     * @param authorizationHeader значение заголовка Authorization
     * @return map с claims или null если токен невалиден
     */
    public Map<String, Object> parseToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            return null;
        }

        final String token = authorizationHeader.substring(BEARER_PREFIX.length());

        try {
            final Claims claims;
            if (validateSignature) {
                claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
                log.debug("JWT signature validated successfully");
            } else {
                // Parse without signature validation (unsafe mode for Gateway-validated tokens)
                final int lastDotIndex = token.lastIndexOf('.');
                if (lastDotIndex == -1) {
                    log.warn("Invalid JWT format - no signature separator");
                    return null;
                }
                final String unsignedToken = token.substring(0, lastDotIndex + 1);
                claims = Jwts.parser()
                    .unsecured()
                    .build()
                    .parseUnsecuredClaims(unsignedToken)
                    .getPayload();
                log.debug("JWT parsed without signature validation");
            }

            return Map.copyOf(claims);
        } catch (JwtException e) {
            log.warn("Failed to parse JWT token: {}", e.getMessage());
            return null;
        }
    }
}
