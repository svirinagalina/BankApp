package ru.katacademy.apigateway.health.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import ru.katacademy.apigateway.filter.HealthAwareFallbackFilter;
import ru.katacademy.apigateway.health.DownstreamServiceRegistry;

@Configuration
public class GatewayFilterConfig {

    /**
     * Глобальный фильтр для health-aware проверки сервисов.
     *
     * Все запросы через Gateway будут проходить через этот фильтр.
     * Если сервис не готов, запрос блокируется с 503.
     */
    @Bean
    public GlobalFilter healthAwareGlobalFilter(DownstreamServiceRegistry registry) {
        return new HealthAwareFallbackFilter(registry);
    }
}