package ru.katacademy.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Конфигурационный класс для глобальной настройки CORS
 * использующем WebFlux
 *
 * Управлет тем, какие домены могут отправлять запросы к приложению,
 * какие методы и заголовки разрешены.
 *
 * Автор: Быстров М
 * Дата: 20.06.2025
 */
@Configuration
public class CorsGlobalConfiguration {

    /**
     * Создаёт конфигурацию CORS, применяемую ко всем маршрутам (/**).
     *
     * @return CorsConfigurationSource — CORS-конфигурация, применяемая ко всем URL.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Допустимые источники (origin). "*" — разрешает все.
        config.setAllowedOrigins(List.of("*"));

        // Допустимые HTTP-методы.
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Любые заголовки (Authorization, Content-Type,...).
        config.setAllowedHeaders(List.of("*"));

        // Передача учётных данных (cookies, заголовки авторизации).
        config.setAllowCredentials(true);

        // Время (в секундах), в течение которого браузер
        // может кэшировать результат
        config.setMaxAge(3600L);

        // Применяет конфигурацию ко всем путям.
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
