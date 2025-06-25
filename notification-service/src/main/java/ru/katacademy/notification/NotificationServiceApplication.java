package ru.katacademy.notification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("ru.katacademy.notification.infrastructure.persistence.repository")
@EntityScan("ru.katacademy.notification.infrastructure.persistence.entity")
public class NotificationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner logProfile(@Value("${spring.profiles.active:}") String profile) {
        return args -> System.out.println(">>> Active Spring profile: " + profile);
    }
}