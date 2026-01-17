package ru.katacademy.apigateway.dto;

import org.springframework.stereotype.Component;
import ru.katacademy.apigateway.config.SwaggerServicesProperties;

import java.util.List;

@Component
public class SwaggerEndpointResolver {

    private final SwaggerServicesProperties props;

    public SwaggerEndpointResolver(SwaggerServicesProperties props) {
        this.props = props;
    }

    public List<SwaggerLink> resolveSwaggerLinks() {
        return props.getServices().entrySet().stream()
                .map(e -> new SwaggerLink(
                        e.getKey(),
                        e.getValue() + "/swagger-ui/index.html"
                ))
                .toList();
    }
}
