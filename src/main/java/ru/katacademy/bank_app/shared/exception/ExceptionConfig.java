package ru.katacademy.bank_app.shared.exception;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.katacademy.bank_shared.exception.GlobalExceptionHandler;

@Configuration
public class ExceptionConfig {

    /**
     * Явно регистрируем GlobalExceptionHandler,
     * чтобы он оказался в контексте user-service.
     */
    @Bean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }
}