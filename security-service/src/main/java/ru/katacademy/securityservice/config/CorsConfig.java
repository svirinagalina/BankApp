package ru.katacademy.securityservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * - CorsConfig - для настройки HTTP-запросов между фронтендом и бэкэндом.
 * - WebMvcConfigurer - интерфейс для интеграции с Spring MVC
 * После запуска микросервиса, данный конфигурационный клас, разрешает доступ ко
 * всем эндпоинтам http://localhost:8080
 *
 * методы:
 * - addCorsMappings() - переопределяет стандартные настройки CORS,
 * устанавливает правила
 * - @param registry - для регистрации настроек.
 *
 * Автор: Быстров М
 * Дата: 04.06.2025
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final Environment env;

    public CorsConfig(Environment env) {
        this.env = env;
    }


    /**
     * - addMapping("/**") - применение ко всем REST -путям
     * - allowedOrigins("http://localhost:8080") - источник запросов(swagger)
     * - allowedMethods("POST") - список методов,
     * для использования в запросах.
     *
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        final List<String> allowedOrigins;
        if (isProdProfile()) {
            allowedOrigins = List.of(
                    "https://frontend.example.com",
                    "https://admin.example.com"
            );
        } else {
            allowedOrigins = List.of(
                    "http://localhost:3000",
                    "http://localhost:8080"
            );
        }

        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins.toArray(new String[0]))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Authorization", "Content-Type", "Accept")
                .allowCredentials(true);
    }

    private boolean isProdProfile() {
        return env.acceptsProfiles("prod");
    }
}