package ru.katacademy.securitystarter.config;

import feign.RequestInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import ru.katacademy.securitystarter.feign.FeignSecurityInterceptor;

/**
 * Автоматическая конфигурация Feign security interceptor.
 *
 * <p>Активируется автоматически при наличии:
 * <ul>
 *   <li>Feign в classpath (RequestInterceptor.class)</li>
 *   <li>spring.application.name в конфигурации</li>
 * </ul>
 *
 * <p>Создает FeignSecurityInterceptor, который автоматически добавляет
 * security-заголовки ко всем исходящим Feign-запросам.
 *
 * @author Galina
 * @since 2026-02-04
 */
@AutoConfiguration
@ConditionalOnClass(RequestInterceptor.class)
@ConditionalOnProperty(name = "spring.application.name")
public class FeignSecurityAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(FeignSecurityAutoConfiguration.class);

    /**
     * Создает bean FeignSecurityInterceptor для пропагации security headers.
     *
     * @param serviceName имя текущего сервиса (spring.application.name)
     * @return настроенный interceptor
     */
    @Bean
    public RequestInterceptor feignSecurityInterceptor(
            @Value("${spring.application.name}") String serviceName) {
        log.info("Registering FeignSecurityInterceptor with serviceName: {}", serviceName);
        return new FeignSecurityInterceptor(serviceName);
    }
}
