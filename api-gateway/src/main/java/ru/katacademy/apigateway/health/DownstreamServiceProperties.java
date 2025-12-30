package ru.katacademy.apigateway.health;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

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

    /**
     * Конфигурация одного downstream-сервиса
     * Вложенный класс позволяющий определить путь до сервиса
     * baseUrl - путь к сервису, берется из конфигурации
     * readinessPath - позволяет проверить состояние сервиса
     */
    public static class ServiceConfig {

        /**
         * Базовый URL сервиса (зависит от профиля: local / docker)
         */
        private String baseUrl;
        /**
         * Endpoint readiness.
         * По умолчанию используется стандартный actuator readiness endpoint.
         */
        private String readinessPath = "/actuator/health/readiness";

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getReadinessPath() {
            return readinessPath;
        }

        public void setReadinessPath(String readinessPath) {
            this.readinessPath = readinessPath;
        }
    }
}