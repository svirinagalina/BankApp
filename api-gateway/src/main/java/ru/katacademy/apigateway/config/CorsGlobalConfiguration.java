package ru.katacademy.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Конфигурационный класс для глобальной настройки CORS
 * использующем WebFlux
 * <p>
 * Управлет тем, какие домены могут отправлять запросы к приложению,
 * какие методы и заголовки разрешены.
 * <p>
 * Автор: Быстров М
 * Дата: 20.06.2025
 */
@Configuration
public class CorsGlobalConfiguration {

    private final Environment env;

    @Value("${cors.allowed-origins:http://localhost:3000}")
    private List<String> allowedOrigins;

    public CorsGlobalConfiguration(Environment env) {
        this.env = env;
    }

    /**
     * Создаёт конфигурацию CORS, применяемую ко всем маршрутам (/**).
     *
     * @return CorsConfigurationSource — CORS-конфигурация, применяемая ко всем URL.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        // Ограничиваем методы
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Ограничиваем заголовки
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        final String[] origins = env.getProperty("cors.allowed-origins", String[].class, new String[]{"http://localhost:3000"});
        config.setAllowedOrigins(List.of(origins));

        config.setMaxAge(3600L);

        // Применяет конфигурацию ко всем путям.
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public CorsWebFilter corsWebFilter(CorsConfigurationSource corsConfigurationSource) {
        return new CorsWebFilter(corsConfigurationSource);
    }
}
