package ru.katacademy.bank_app.accountservice.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import java.util.List;

/**
 * Конфигурационный класс CORS-фильтра для разрешения кросс-доменных запросов (CORS).
 *
 * Назначение:
 * - Настраивает CORS для REST API.
 * - Обеспечивает доступ к API из Swagger UI на `http://localhost:8080`.
 *
 * Автор: Быстров М.
 * Дата: 2025-06-10
 */
@Configuration
public class CorsFilterConfig {

    /**
     * Создаёт и настраивает CORS-фильтр.
     *
     * @return CorsFilter с заданной конфигурацией.
     *
     * Конфигурация включает:
     * - allowedOrigins: Разрешает запросы с `http://localhost:8080`.
     * - allowedMethods: Разрешает HTTP-методы (`GET`, `POST`, `PUT` и т.д.).
     * - allowedHeaders: Разрешает любые заголовки.
     * - allowCredentials: Включает поддержку cookies и заголовков.
     *
     * Зарегистрированные пути:
     * - `/v3/api-docs/**`: доступ к OpenAPI (Swagger).
     * - `/**`: разрешение ко всем REST endpoint (на всякий случай).
     */
    @Bean
    public CorsFilter corsFilter() {
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:8080"));
        config.setAllowedMethods(List.of("*"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/v3/api-docs/**", config);
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
