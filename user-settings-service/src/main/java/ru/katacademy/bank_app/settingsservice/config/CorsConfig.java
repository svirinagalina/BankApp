package ru.katacademy.bank_app.settingsservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
 * Дата: 03.06.2025
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    /**
     * - addMapping("/**") - применение ко всем REST -путям
     * - allowedOrigins("http://localhost:8080") - источник запросов(swagger)
     * - allowedMethods("GET", "POST", "DELETE", "PATCH") - список методов,
     * для использования в запросах.
     *
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8080")
                .allowedMethods("GET", "POST", "DELETE", "PATCH");
    }
}
