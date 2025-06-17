package ru.katacademy.securityservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Тестовый SecurityConfig, отключающий редирект на форму логина.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Отключаем форму логина (редирект на /login) и разрешаем все запросы.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)                         // отключаем CSRF для тестов
                .formLogin(AbstractHttpConfigurer::disable)                   // убираем редирект на форму логина
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())// разрешаем всё без авторизации
                .build();
    }
}