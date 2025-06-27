package ru.katacademy.bank_app.accountservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = "ru.katacademy.bank_app.accountservice.domain.repository")
public class JpaRepositoryConfig {
}
