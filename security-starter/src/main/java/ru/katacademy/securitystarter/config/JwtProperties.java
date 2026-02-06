package ru.katacademy.securitystarter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Конфигурационные свойства для JWT authentication.
 *
 * <p>Используется для настройки JwtParser и JwtUserIdentityResolver.
 *
 * <p>Пример конфигурации:
 * <pre>
 * bank:
 *   security:
 *     jwt:
 *       secret: your-secret-key-minimum-32-characters
 *       validate-signature: false  # если Gateway уже проверил подпись
 * </pre>
 *
 * @author Galina
 * @since 2026-02-04
 */
@ConfigurationProperties(prefix = "bank.security.jwt")
public class JwtProperties {

    /**
     * Секретный ключ для валидации JWT подписи.
     * Минимальная длина: 32 символа для HMAC-SHA256.
     */
    private String secret;

    /**
     * Включить валидацию подписи JWT токена.
     * Если false, подпись не проверяется (полезно когда Gateway уже проверил).
     */
    private boolean validateSignature = true;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public boolean isValidateSignature() {
        return validateSignature;
    }

    public void setValidateSignature(boolean validateSignature) {
        this.validateSignature = validateSignature;
    }
}
