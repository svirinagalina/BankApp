package ru.katacademy.securityservice.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CorsFilter;

@TestConfiguration
public class TestCorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        return new CorsFilter(request -> null);
    }
}
