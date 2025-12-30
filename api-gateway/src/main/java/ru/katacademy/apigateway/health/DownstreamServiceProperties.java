package ru.katacademy.apigateway.health;

import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.katacademy.apigateway.health.config.ServiceConfig;

import java.util.Map;

/**
 * Properties-класс для описания downstream-сервисов.
 *
 * Хранит логическую модель сервисов, с которыми работает API Gateway,
 * и позволяет получать их адреса и lifecycle endpoints из конфигурации.
 *
 * Используется для health-aware маршрутизации.
 *
 * Автор: Krasitskii Dmitrii
 * Дата: 29.12.2025
 */
@ConfigurationProperties (prefix = "gateway.downstream")
public class DownstreamServiceProperties {
    /**
     * Ключ - название сервиса например "account-service"
     * Значение - конфигурация сервиса
     */
    private Map<String, ServiceConfig> services;

    public Map<String, ServiceConfig> getServices() {
        return services;
    }

    public void setServices(Map<String, ServiceConfig> services) {
        this.services = services;
    }
}