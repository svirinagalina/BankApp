package ru.katacademy.apigateway.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Кастомный HealthIndicator для отображения статусов downstream-сервисов
 */
@Component
public class DownstreamServicesHealthIndicator implements HealthIndicator {

    private final DownstreamServiceRegistry registry;

    public DownstreamServicesHealthIndicator(DownstreamServiceRegistry registry) {
        this.registry = registry;
    }

    @Override
    public Health health() {
        Map<String, ServiceStatus> allStatuses = registry.getAll();

        if (allStatuses.isEmpty()) {
            return Health.unknown()
                    .withDetail("message", "No downstream services configured")
                    .build();
        }

        boolean allReady = allStatuses.values()
                .stream()
                .allMatch(status -> status == ServiceStatus.READY);

        Health.Builder healthBuilder = allReady ?
                Health.up() : Health.down();

        allStatuses.forEach((service, status) ->
                healthBuilder.withDetail(service, status.toString()));

        return healthBuilder.build();
    }
}