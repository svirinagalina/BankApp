package ru.katacademy.securitystarter.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import ru.katacademy.securitystarter.identity.JwtUserIdentityResolver;
import ru.katacademy.securitystarter.jwt.JwtParser;

/**
 * Автоматическая конфигурация JWT support.
 *
 * <p>Активируется при наличии:
 * <ul>
 *   <li>io.jsonwebtoken.Jwts в classpath</li>
 *   <li>bank.security.jwt.secret в конфигурации</li>
 * </ul>
 *
 * <p>Создает JwtParser и JwtUserIdentityResolver для поддержки JWT authentication.
 *
 * @author Galina
 * @since 2026-02-04
 */
@AutoConfiguration
@ConditionalOnClass(name = "io.jsonwebtoken.Jwts")
@ConditionalOnProperty(name = "bank.security.jwt.secret")
@EnableConfigurationProperties(JwtProperties.class)
public class JwtAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(JwtAutoConfiguration.class);

    /**
     * Создает bean JwtParser для парсинга JWT токенов.
     *
     * @param properties JWT properties
     * @return настроенный JWT parser
     */
    @Bean
    public JwtParser jwtParser(JwtProperties properties) {
        log.info("Registering JwtParser with validateSignature: {}", properties.isValidateSignature());
        return new JwtParser(properties.getSecret(), properties.isValidateSignature());
    }

    /**
     * Создает bean JwtUserIdentityResolver для извлечения userId из JWT.
     *
     * @param jwtParser JWT parser
     * @return resolver с поддержкой JWT
     */
    @Bean
    public JwtUserIdentityResolver jwtUserIdentityResolver(JwtParser jwtParser) {
        log.info("Registering JwtUserIdentityResolver");
        return new JwtUserIdentityResolver(jwtParser);
    }
}
