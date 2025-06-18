package ru.katacademy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(
        basePackages = "ru.katacademy.infrastructure.repository")
public class AuthStatisticsService {
    public static void main(String[] args) {
        SpringApplication.run(AuthStatisticsService.class, args);
    }
}