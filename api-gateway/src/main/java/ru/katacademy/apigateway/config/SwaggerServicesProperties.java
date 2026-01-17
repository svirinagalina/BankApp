package ru.katacademy.apigateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "gateway.swagger")
public class SwaggerServicesProperties {

    private Map<String, String> services;

    public Map<String, String> getServices() {
        return services;
    }

    public void setServices(Map<String, String> services) {
        this.services = services;
    }
}
