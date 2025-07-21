package ru.katacademy.securityservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.katacademy.securityservice.security.JwtAuthenticationFilter;
import ru.katacademy.securityservice.util.JwtUtil;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

/**
 * Конфигурация Spring Security для stateless/JWT аутентификации.
 * <ul>
 *   <li>CSRF-режим выключен (stateless, JWT).</li>
 *   <li>SessionCreationPolicy — STATELESS.</li>
 *   <li>Публичные end­point’ы:
 *     <ul>
 *       <li>POST /api/users/register</li>
 *       <li>POST /api/security/verify</li>
 *       <li>GET  /api/accounts/test</li>
 *     </ul>
 *   </li>
 *   <li>Все остальные запросы требуют JWT‑аутентификации.</li>
 *   <li>При отсутствии или некорректном токене возвращается 401 Unauthorized.</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    /**
     * Фильтр, извлекающий и валидирующий JWT‑токен из заголовка Authorization.
     *
     * @return экземпляр JwtAuthenticationFilter
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil);
    }

    /**
     * Основная цепочка фильтров безопасности.
     *
     * @param http объект HttpSecurity для настройки
     * @return настроенный SecurityFilterChain
     * @throws Exception при ошибках конфигурации
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(POST, "/api/users/register").permitAll()
                        .requestMatchers(POST, "/api/security/verify").permitAll()
                        .requestMatchers(GET,  "/api/accounts/test").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        jwtAuthenticationFilter(),
                        UsernamePasswordAuthenticationFilter.class
                )
                .build();
    }
}
