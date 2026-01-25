package ru.katacademy.securitystarter.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.katacademy.securitystarter.filter.AuthenticationFilter;
import ru.katacademy.securitystarter.identity.HeaderUserIdentityResolver;
import ru.katacademy.securitystarter.identity.UserIdentityResolver;

/**
 * Автоматическая конфигурация security-starter.
 *
 * Регистрирует необходимые компоненты для минимальной аутентификации:
 * - UserIdentityResolver (Header-based по умолчанию)
 * - AuthenticationFilter
 * - SecurityFilterChain с базовыми настройками
 *
 * Активируется автоматически при подключении security-starter как зависимости.
 *
 * @author Galina
 * @date 2026-01-23
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityAutoConfiguration {

    /**
     * Создает bean UserIdentityResolver для извлечения userId из заголовков.
     *
     * @return реализация HeaderUserIdentityResolver
     */
    @Bean
    public UserIdentityResolver userIdentityResolver() {
        // TODO: Вернуть новый экземпляр HeaderUserIdentityResolver
        return new HeaderUserIdentityResolver();
    }

    /**
     * Создает bean AuthenticationFilter для обработки аутентификации.
     *
     * @param userIdentityResolver resolver для извлечения userId
     * @return настроенный фильтр аутентификации
     */
    @Bean
    public AuthenticationFilter authenticationFilter(UserIdentityResolver userIdentityResolver) {
        // TODO: Вернуть новый экземпляр AuthenticationFilter с resolver
        return new AuthenticationFilter(userIdentityResolver);
    }

    /**
     * Настраивает цепочку фильтров Spring Security.
     *
     * Конфигурация:
     * - Отключает CSRF (для упрощения в MVP)
     * - Разрешает все запросы (авторизация на уровне бизнес-логики)
     * - Добавляет AuthenticationFilter перед стандартным фильтром
     *
     * @param http объект конфигурации безопасности
     * @param authenticationFilter наш кастомный фильтр
     * @return настроенная цепочка фильтров
     * @throws Exception если конфигурация невалидна
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthenticationFilter authenticationFilter) throws Exception {
        // TODO: Настроить HttpSecurity

        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}