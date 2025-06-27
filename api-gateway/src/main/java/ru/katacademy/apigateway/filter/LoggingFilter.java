package ru.katacademy.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Глобальный фильтр для логирования входящих запросов проходящим через API Gateway.
 * Логирует URI каждого запроса в стандартный вывод.
 *
 * Автор: Быстров М
 * Дата: 20.06.2025
 */
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    /**
     * Определяет порядок выполнения фильтра в цепочке.
     * Фильтры с меньшим значением выполняются раньше. Значение -1 означает, что фильтр будет
     * выполнен раньше большинства других стандартных фильтров.
     */
    private static final int LOGGING_FILTER_ORDER = -1;

    /**
     * @param exchange объект ServerWebExchange, содержащий информацию о запросе и ответе.
     * @param chain цепочка фильтров, для передачи управления после выполнения логики.
     * @return Mono<Void> — завершение обработки фильтра.
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        System.out.println("Gateway request: " + exchange.getRequest().getURI());
        return chain.filter(exchange);
    }

    /**
     * @return int — возвращаем порядок, чем меньше значение, тем выше приоритет.
     */
    @Override
    public int getOrder() {
        return LOGGING_FILTER_ORDER;
    }
}