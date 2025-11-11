package ru.katacademy.apigateway.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурационный компонент, слушающий события Circuit Breaker'а (Resilience4j)
 * и логирующий все его состояния и переходы.
 *
 * <p>Данный класс подписывается на события и позволяет отслеживать:
 * <ul>
 *     <li>Переходы состояний (CLOSED → OPEN → HALF_OPEN → ...);</li>
 *     <li>Ошибки вызовов, отказы, успешные и повторные попытки;</li>
 *     <li>Процент ошибок, после которых breaker «размыкает цепь».</li>
 * </ul>
 */

@Configuration
public class CircuitBreakerEventsConfig {
    private static final Logger log = LoggerFactory.getLogger(CircuitBreakerEventsConfig.class);

    @Bean
    public ApplicationRunner subscribeToCircuitBreakerEvents(CircuitBreakerRegistry registry) {
        return args -> {
            registry.getAllCircuitBreakers().forEach(cb -> cb.getEventPublisher()
                    .onStateTransition(evt -> log.info("CircuitBreaker '{}' state transition: {}", cb.getName(), evt.getStateTransition())));
        };
    }
}

