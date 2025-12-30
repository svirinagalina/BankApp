package ru.katacademy.apigateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.katacademy.apigateway.health.DownstreamServiceRegistry;

import java.util.Map;

/**
 * endpoint для просмотра статусов
 */
@RestController
@RequestMapping("/actuator")
public class ServiceStatusController {

    private final DownstreamServiceRegistry registry;

    public ServiceStatusController(DownstreamServiceRegistry registry) {
        this.registry = registry;
    }

    @GetMapping("/service-status")
    public Map<String, String> getServiceStatuses() {
        return registry.getAll().entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().toString()
                ));
    }

    @GetMapping("/downstream-health")
    public Map<String, Object> getDownstreamHealth() {
        Map<String, String> statuses = getServiceStatuses();

        long readyCount = statuses.values().stream()
                .filter("READY"::equals)
                .count();
        long totalCount = statuses.size();

        return Map.of(
                "status", readyCount == totalCount ? "UP" : "DOWN",
                "readyServices", readyCount,
                "totalServices", totalCount,
                "services", statuses
        );
    }
}