package ru.katacademy.securityservice.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

/**
 * JwtUtil —  генерация и валидация JWT-токенов.
 *
 *  - Создаёт подписанные JWT-токены на основе заданного subject(например имя пользователя);
 *  - Проверяет валидность (подпись, срок действия);
 *  - Извлекает из токена данные (claims);
 *  - Использует алгоритм HMAC-SHA256 и секрет, заданный в application.yml.
 *
 * Источник секрета:
 *  - @Value("${jwt.secret}") — задаётся в конфигурации
 *
 * Источник времени жизни:
 *  - @Value("${jwt.expiration-ms}") — по умолчанию 3600000 мс (1 час)
 *
 * Пример секции конфигурации:
 *  jwt:
 *    secret: "testsecretkeyfortestpurposesonly1234567890"
 *    expiration-ms: 3600000
 *
 * Автор: Быстров М.
 * Дата: 10.06.2025
 */
@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    /**
     * Секретный ключ для HMAC-подписи.
     * Задаётся через application.yml как jwt.secret.
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * Время жизни токена в миллисекундах.
     * По умолчанию — 3600000 (1 час).
     */
    @Value("${jwt.expiration-ms:3600000}")
    private long expirationMs;

    /**
     * Ключ, сгенерированный из секрета, используется для подписи/валидации токенов.
     */
    private Key signingKey;

    /**
     * Возвращает объект ключа для подписи на основе секрета.
     * Используется алгоритм HMAC-SHA256.
     */
    private Key getSigningKey() {
        if (signingKey == null) {
            this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            log.info("JWT signing key initialized");
        }
        return signingKey;
    }


    /**
     * Генерирует новый JWT-токен для заданного пользователя (subject).
     *
     * @param subject логин или ID пользователя
     * @return строка токена (compact JWT)
     */
    public String generateToken(String subject, Long userId, Collection<String> roles) {
        return Jwts.builder()
                .setSubject(subject)
                .claim("userId", userId)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Возвращает все claims из валидного токена.
     *
     * @param token строка JWT-токена
     * @return Map из всех ключей и значений (например, sub, iat, exp)
     * @throws JwtException если токен просрочен, недействителен или подделан
     */
    public Map<String, Object> getClaimsCopy(String token) {
        final Claims claims = parseToken(token).getBody();
        return new HashMap<>(claims);
    }

    /**
     * Извлекает из токена `subject` (например, имя пользователя).
     *
     * @param token строка JWT-токена
     * @return subject, заданный при создании
     * @throws JwtException при недействительном токене
     */
    public String getSubject(String token) {
        return parseToken(token).getBody().getSubject();
    }

    /**
     * Парсит токен, проверяет подпись и возвращает объект Jws<Claims>.
     *
     * @param token строка JWT-токена
     * @return расшифрованный и проверенный токен
     * @throws JwtException при ошибках подписи или истечении срока действия
     */
    private Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
    }

    public Long getUserId(String token) {
        return parseToken(token).getBody().get("userId", Long.class);
    }
    public List<String> getRoles(String token) {
        final Claims claims = parseToken(token).getBody();
        final Object roles = claims.get("roles");
        if (roles instanceof Collection<?>) {
            return ((Collection<?>) roles).stream().map(Object::toString).collect(Collectors.toList());
        }
        return List.of();
    }
}
