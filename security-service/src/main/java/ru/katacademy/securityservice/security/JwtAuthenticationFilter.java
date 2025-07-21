package ru.katacademy.securityservice.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.katacademy.securityservice.util.JwtUtil;

import java.io.IOException;
import java.util.Collections;

/**
 * Фильтр JWT-авторизации, выполняющийся один раз для каждого запроса.
 * <p>
 * Извлекает JWT из заголовка Authorization (Bearer-token),
 * проверяет его валидность и, в случае успеха,
 * устанавливает аутентификацию в контексте безопасности.
 * Некорректные или просроченные токены игнорируются
 * и приводят к отказу в доступе далее по цепочке.
 * </p>
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * Утилита для генерации и валидации JWT-токенов.
     */
    private final JwtUtil jwtUtil;

    /**
     * Основной метод фильтрации: извлекает токен, проверяет и устанавливает аутентификацию.
     *
     * @param request  incoming HTTP request
     * @param response HTTP response
     * @param chain    фильтрационная цепочка
     * @throws ServletException при ошибке сервлета
     * @throws IOException      при I/O ошибке
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        final String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            final String token = header.substring(7);
            try {
                final String username = jwtUtil.getSubject(token);
                final UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                Collections.emptyList()
                        );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } catch (JwtException ex) {
            }
        }
        chain.doFilter(request, response);
    }
}