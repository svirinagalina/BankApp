package ru.katacademy.securityservice.health;

import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * HealthIndicator для проверки конфигурации JWT-секрета.
 * <p>
 * Используется в Spring Boot Actuator для возврата состояния сервиса
 * по эндпоинту {@code /actuator/health}.
 * </p>
 *
 * <p>
 * Проверяется, что указанный {@code jwt.secret} можно использовать для создания
 * HMAC-ключа с помощью {@link io.jsonwebtoken.security.Keys#hmacShaKeyFor(byte[])}.
 * Это гарантирует, что секрет соответствует требованиям алгоритма подписи JWT (например, длине).
 * </p>
 *
 * <p>
 * Кеширует результат с помощью Spring Cache API, если статус не {@code UP}.
 * Это снижает нагрузку при повторных запросах к /health в случае ошибок конфигурации.
 * </p>
 *
 * <p>
 * В случае ошибки возвращается статус {@code DOWN} с причиной и исключением.
 * </p>
 *
 * <p><b>Пример:</b></p>
 * <pre>{@code
 * {
 *   "status": "UP",
 *   "components": {
 *     "jwtHealthIndicator": {
 *       "status": "UP",
 *       "details": {
 *         "message": "JWT secret is valid"
 *       }
 *     }
 *   }
 * }
 * }</pre>
 */

@Component
public class JwtHealthIndicator implements HealthIndicator {

    private static final Logger log = LoggerFactory.getLogger(JwtHealthIndicator.class);

    /**
     * Секрет для подписи JWT, берется из конфигурации приложения.
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * Проверяет валидность JWT-секрета.
     * <p>
     * Метод создаёт HMAC-ключ из секрета. Если операция успешна — статус {@code UP},
     * иначе — {@code DOWN} с деталями ошибки.
     * </p>
     *
     * @return объект {@link Health} со статусом {@code UP} или {@code DOWN}.
     */
    @Override
    @Cacheable(value = "jwtHealthCache", unless = "#result.status == 'UP'")
    public Health health() {
        try {
            Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            return Health.up().withDetail("message", "JWT secret is valid").build();
        } catch (Exception e) {
            log.error("JWT health check failed", e);
            return Health.down()
                    .withDetail("reason", "Invalid JWT configuration")
                    .withException(e)
                    .build();
        }
    }
}