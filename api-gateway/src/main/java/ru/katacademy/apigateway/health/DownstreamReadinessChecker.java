package ru.katacademy.apigateway.health;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.katacademy.apigateway.dto.ReadinessResponse;
import ru.katacademy.apigateway.health.config.ServiceConfig;

import java.time.Duration;
import java.util.Map;

/**
 * Компонент, отвечающий за периодическую проверку readiness downstream-сервисов.
 *
 * <p>
 * Периодически опрашивает endpoint
 * {@code /actuator/health/readiness} каждого сконфигурированного сервиса
 * и обновляет их состояние в {@link DownstreamServiceRegistry}.
 * </p>
 *
 * <p>
 * Данный компонент не участвует в маршрутизации напрямую.
 * Его задача — поддерживать актуальное состояние сервисов,
 * которое затем используется gateway-фильтрами.
 * </p>
 *
 * Автор: Krasitskii Dmitrii
 * Дата: 29.12.2025
 */
@Component
public class DownstreamReadinessChecker {

    private static final Logger log =
            LoggerFactory.getLogger(DownstreamReadinessChecker.class);

    private final DownstreamServiceProperties properties;
    private final DownstreamServiceRegistry registry;
    private final WebClient webClient;

    /**
     * Конструктор с внедрением зависимостей.
     *
     * @param properties описание downstream-сервисов из конфигурации
     * @param registry   реестр состояний сервисов
     * @param webClient  HTTP-клиент для выполнения readiness-запросов
     */
    public DownstreamReadinessChecker(DownstreamServiceProperties properties,
                                      DownstreamServiceRegistry registry,
                                      WebClient webClient) {
        this.properties = properties;
        this.registry = registry;
        this.webClient = webClient;
    }

    /**
     * Первичная проверка readiness всех сервисов сразу после старта gateway.
     *
     * <p>
     * Выполняется один раз после инициализации бина.
     * Позволяет gateway получить актуальное состояние сервисов
     * ещё до обработки первых пользовательских запросов.
     * </p>
     */
    @PostConstruct
    public void initialCheck() {
        checkAllServices();
    }

    /**
     * Периодическая проверка readiness downstream-сервисов.
     *
     * <p>
     * Запускается с фиксированной задержкой между завершением предыдущего
     * и началом следующего выполнения.
     * Интервал настраивается через параметр
     * {@code gateway.health-check.delay}.
     * </p>
     */
    @Scheduled(fixedDelayString = "${gateway.health-check.delay:5000}")
    public void scheduledCheck() {
        checkAllServices();
    }

    /**
     * Запускает проверку readiness для всех сервисов,
     * описанных в {@link DownstreamServiceProperties}.
     */
    private void checkAllServices() {
        Map<String, ServiceConfig> services =
                properties.getServices();

        if (services == null || services.isEmpty()) {
            log.warn("No downstream services configured for readiness check");
            return;
        }

        services.forEach(this::checkSingleService);
    }

    /**
     * Проверяет readiness конкретного сервиса.
     *
     * <p>
     * В случае любой ошибки (таймаут, сетевые проблемы, недоступность сервиса)
     * сервис помечается как {@link ServiceStatus#UNAVAILABLE}.
     * </p>
     *
     * @param serviceName логическое имя сервиса
     * @param config      конфигурация сервиса
     */
    private void checkSingleService(String serviceName,
                                    ServiceConfig config) {

        if (config.getBaseUrl() == null) {
            log.warn("Service '{}' has no baseUrl configured, marking as UNAVAILABLE", serviceName);
            registry.update(serviceName, ServiceStatus.UNAVAILABLE);
            return;
        }

        String readinessUrl = config.getBaseUrl() + config.getReadinessPath();

        webClient
                .get()
                .uri(readinessUrl)
                .retrieve()
                .bodyToMono(ReadinessResponse.class)
                .timeout(Duration.ofSeconds(3))
                .map(response -> mapStatus(response.getStatus()))
                .onErrorResume(ex -> {
                    log.warn("Service '{}' readiness check failed: {}", serviceName, ex.getMessage());
                    return Mono.just(ServiceStatus.UNAVAILABLE);
                })
                .subscribe(status -> updateStatus(serviceName, status));
    }

    /**
     * Преобразует статус Actuator во внутренний {@link ServiceStatus}.
     *
     * @param actuatorStatus статус, возвращаемый Spring Boot Actuator
     * @return внутреннее состояние сервиса
     */
    private ServiceStatus mapStatus(String actuatorStatus) {
        if ("UP".equals(actuatorStatus)) {
            return ServiceStatus.READY;
        }
        return ServiceStatus.NOT_READY;
    }

    /**
     * Обновляет состояние сервиса в реестре и логирует изменение статуса.
     *
     * @param serviceName имя сервиса
     * @param newStatus   новое состояние сервиса
     */
    private void updateStatus(String serviceName, ServiceStatus newStatus) {
        ServiceStatus oldStatus = registry.get(serviceName);

        if (oldStatus != newStatus) {
            log.info("Service '{}' status changed: {} -> {}", serviceName, oldStatus, newStatus);
        }

        registry.update(serviceName, newStatus);
    }
}