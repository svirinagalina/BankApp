package ru.katacademy.apigateway.dto;

import org.springframework.stereotype.Component;
import ru.katacademy.apigateway.config.SwaggerServicesProperties;

import java.util.List;

/**
 * Компонент для формирования ссылок на Swagger UI downstream-сервисов.
 *
 * Использует {@link SwaggerServicesProperties} для получения списка сервисов
 * и формирования полной ссылки на их Swagger UI.
 * author: Krasitskii Dmitrii
 * date: 17.01.2026
 */
@Component
public class SwaggerEndpointResolver {

    private final SwaggerServicesProperties props;

    public SwaggerEndpointResolver(SwaggerServicesProperties props) {
        this.props = props;
    }

    /**
     * Формирует список ссылок на Swagger UI всех downstream-сервисов.
     *
     * @return список {@link SwaggerLink} с именами сервисов и URL на их Swagger UI
     */
    public List<SwaggerLink> resolveSwaggerLinks() {
        return props.getServices().entrySet().stream()
                .map(e -> new SwaggerLink(
                        e.getKey(),
                        e.getValue() + "/swagger-ui/index.html"
                ))
                .toList();
    }
}
