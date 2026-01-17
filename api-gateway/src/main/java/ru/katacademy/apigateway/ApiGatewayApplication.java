package ru.katacademy.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import ru.katacademy.apigateway.dto.SwaggerEndpointResolver;


@SpringBootApplication
@EnableScheduling
@ConfigurationPropertiesScan
public class ApiGatewayApplication {

    private static final Logger log =
            LoggerFactory.getLogger(ApiGatewayApplication.class);

    private final SwaggerEndpointResolver swaggerResolver;

    public ApiGatewayApplication(SwaggerEndpointResolver swaggerResolver) {
        this.swaggerResolver = swaggerResolver;
    }

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logSwaggerEndpoints() {
        String swaggerUIDocker = "http://localhost";
        String swaggerUILocal = "http://localhost:8080";
        String format = "| %-25s | %-45s |";
        String line = "+---------------------------+-----------------------------------------------+";

        log.info("");
        log.info("Available downstream Swagger UI endpoints");
        log.info("");
        log.info("Swagger point, with a drop-down list of all available services from profile DOCKER " + swaggerUIDocker);
        log.info("Swagger point, with a drop-down list of all available services from profile LOCAL " + swaggerUILocal);
        log.info("");

        log.info(line);
        log.info(String.format(format, "Service", "Swagger UI"));
        log.info(line);

        swaggerResolver.resolveSwaggerLinks().forEach(link ->
                log.info(String.format(format, link.name(), link.url()))
        );

        log.info(line);
        log.info("");
    }
}