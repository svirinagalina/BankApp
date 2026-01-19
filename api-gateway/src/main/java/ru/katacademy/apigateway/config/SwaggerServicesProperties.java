package ru.katacademy.apigateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Properties-класс для конфигурации Swagger UI ссылок downstream-сервисов.
 *
 * Загружает из конфигурации (application.yml/profiles) URL-адреса сервисов,
 * на которых доступен Swagger UI, и предоставляет их для использования
 * в логах или в других компонентах.
 * author: Krasitskii Dmitrii
 * date: 17.01.2026
 */

@Configuration
@ConfigurationProperties(prefix = "gateway.swagger")
public class SwaggerServicesProperties {

    private Map<String, String> services;

    public Map<String, String> getServices() {
        return services;
    }

    public void setServices(Map<String, String> services) {
        this.services = services;
    }
}
