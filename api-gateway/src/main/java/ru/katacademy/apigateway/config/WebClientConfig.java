package ru.katacademy.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Конфигурация WebClient для API Gateway.
 *
 * Используется для взаимодействия gateway с downstream-сервисами
 * (health-check, readiness, служебные запросы).
 */
@Configuration
public class WebClientConfig {

    /**
     * Базовый WebClient бин.
     *
     * Используется всеми компонентами gateway, которым требуется
     * неблокирующий HTTP-клиент.
     */
    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }
}