package ru.katacademy.securityservice.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.katacademy.bank_shared.security.CustomUserDetails;
import ru.katacademy.securityservice.util.JwtUtil;

import java.io.IOException;
import java.util.List;
import java.util.Set;

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
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * Утилита для генерации и валидации JWT-токенов.
     */
    private final JwtUtil jwtUtil;

    private static final Set<String> WHITELIST = Set.of(
            "/swagger-ui",
            "/swagger-ui.html",
            "/v3/api-docs",
            "/swagger-resources",
            "/webjars",
            "/configuration/ui",
            "/configuration/security",
            "/api/users/register",
            "/api/security/verify",
            "/api/accounts/test"
    );

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

        log.info("JWT filter called for {}", request.getRequestURI());

        final String path = request.getRequestURI();
        final String method = request.getMethod();

        if ("OPTIONS".equalsIgnoreCase(method)) {
            chain.doFilter(request, response);
            return;
        }

        if (WHITELIST.stream().anyMatch(path::startsWith)) {
            chain.doFilter(request, response);
            return;
        }

        final String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            final String token = header.substring(7);
            try {
                final Long userId = jwtUtil.getUserId(token);
                final String subject = jwtUtil.getSubject(token);
                final List<String> roles = jwtUtil.getRoles(token);

                final var authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();

                final CustomUserDetails customUserDetails = new CustomUserDetails(
                        userId,
                        subject,
                        authorities
                );

                final UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(customUserDetails,
                                null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authToken);

            } catch (JwtException ex) {
                log.warn("JWT parsing/validation failed: {}", ex.getMessage());
                SecurityContextHolder.clearContext();
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired JWT token");
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
