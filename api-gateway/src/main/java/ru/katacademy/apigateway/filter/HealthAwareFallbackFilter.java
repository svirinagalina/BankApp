package ru.katacademy.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.katacademy.apigateway.health.DownstreamServiceRegistry;
import ru.katacademy.apigateway.health.ServiceStatus;

import java.nio.charset.StandardCharsets;

/**
 * Global health-aware фильтр API Gateway.
 *
 * <p>
 * Перед маршрутизацией пользовательского запроса проверяет readiness
 * downstream-сервиса и блокирует трафик, если сервис не готов.
 * </p>
 *
 * <p>
 * Инфраструктурные маршруты (Swagger, Actuator и т.д.)
 * исключены из health-aware проверки.
 * </p>
 *
 * Автор: Krasitskii Dmitrii
 */
@Component
public class HealthAwareFallbackFilter implements GlobalFilter, Ordered {

    private final DownstreamServiceRegistry registry;

    public HealthAwareFallbackFilter(DownstreamServiceRegistry registry) {
        this.registry = registry;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        if (isInfrastructurePath(path)) {
            return chain.filter(exchange);
        }

        String serviceName = extractServiceName(path);
        ServiceStatus status = registry.get(serviceName);

        if (status != ServiceStatus.READY) {
            return fallback(exchange, serviceName);
        }

        return chain.filter(exchange);
    }

    /**
     * Определяет, относится ли путь к инфраструктурным endpoint'ам.
     */
    private boolean isInfrastructurePath(String path) {
        return path == null
                || path.isBlank()
                || path.equals("/")
                || path.startsWith("/swagger")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/webjars")
                || path.startsWith("/actuator");
    }

    /**
     * Извлекает имя сервиса из URI запроса.
     *
     * Примеры:
     *  /account-service/** → account-service
     *  /kyc-service/**     → kyc-service
     */
    private String extractServiceName(String path) {
        String[] parts = path.split("/");
        return parts.length > 1 ? parts[1] : "unknown-service";
    }

    /**
     * Формирует единый fallback-ответ от имени Gateway.
     */
    private Mono<Void> fallback(ServerWebExchange exchange, String serviceName) {
        String body = """
                {
                  "status": "FAILED",
                  "service": "%s",
                  "message": "Service temporarily unavailable. This is a gateway fallback response."
                }
                """.formatted(serviceName);

        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse()
                        .bufferFactory()
                        .wrap(bytes)));
    }

    /**
     * Фильтр должен выполняться как можно раньше.
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}