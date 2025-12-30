package ru.katacademy.apigateway.health.config;

/**
 * Конфигурация одного downstream-сервиса
 * Определяет базовый URL и endpoint readiness.
 */
public class ServiceConfig {

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