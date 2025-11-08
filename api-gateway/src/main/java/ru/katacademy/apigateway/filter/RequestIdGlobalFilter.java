package ru.katacademy.apigateway.filter;

import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Генерирует X-Request-Id, если он отсутствует, и прокидывает header дальше.
 * - Устанавливает header в запрос (mutate request), чтобы backend получил тот же id.
 * - Устанавливает header в response (заменяет существующий) — чтобы клиент увидел id.
 * Глобальный фильтр для добавления уникального идентификатора запроса (Request-ID)
 * в каждый HTTP-запрос, проходящий через API Gateway.
 *
 * <p>Назначение фильтра:
 * <ul>
 *     <li>Генерировать и добавлять заголовок <b>X-Request-Id</b>, если он отсутствует;</li>
 *     <li>Пробрасывать его дальше в каждый микросервис, чтобы трассировать цепочку запросов;</li>
 *     <li>Добавлять этот ID в ответ (response), чтобы клиент видел свой идентификатор запроса.</li>
 * </ul>
 */
@Component
public class RequestIdGlobalFilter implements GlobalFilter, Ordered {

    public static final String HEADER = "X-Request-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String existing = request.getHeaders().getFirst(HEADER);

        if (existing == null || existing.isBlank()) {
            String generated = UUID.randomUUID().toString();
            ServerHttpRequest mutated = request.mutate()
                    .header(HEADER, generated)
                    .build();

            // Устанавливаем в response
            exchange.getResponse().getHeaders().set(HEADER, generated);

            return chain.filter(exchange.mutate().request(mutated).build());
        } else {
            // если уже есть - прокидываем и в ответ ставим тот же
            exchange.getResponse().getHeaders().set(HEADER, existing);
            return chain.filter(exchange);
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}

