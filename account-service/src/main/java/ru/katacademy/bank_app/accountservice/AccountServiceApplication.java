package ru.katacademy.bank_app.accountservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication
@EnableJpaRepositories(basePackages =
        "ru.katacademy.bank_app.accountservice.infrastructure.repository")
@EntityScan(basePackages =
        "ru.katacademy.bank_app.accountservice.infrastructure.persistence.entity")
public class AccountServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);
    }

}
