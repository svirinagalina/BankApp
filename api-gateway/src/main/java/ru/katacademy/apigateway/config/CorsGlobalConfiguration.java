package ru.katacademy.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
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

    private final Environment env;

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

        // Ограничиваем методы
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Ограничиваем заголовки
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));

        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        // Настраиваем allowedOrigins в зависимости от окружения
        final String[] activeProfiles = env.getActiveProfiles();
        if (activeProfiles.length > 0 && activeProfiles[0].equals("prod")) {
            // В продакшене: список доверенных фронтендов
            config.setAllowedOrigins(List.of(
                    "https://frontend1.example.com",
                    "https://frontend2.example.com"
            ));
        } else {
            // В dev: localhost
            config.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:8080"));
        }

        // Применяет конфигурацию ко всем путям.
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
