package ru.katacademy.bank_shared.security;

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
import org.springframework.web.filter.OncePerRequestFilter;

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

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        final String path = request.getRequestURI();
        log.debug("JWT filter triggered for {}", path);

        final String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            final String token = header.substring(7);
            try {
                authenticate(token);
            } catch (JwtException e) {
                log.warn("JWT validation failed: {}", e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                        "Invalid or expired JWT token");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private void authenticate(String token) {
        final Long userId = jwtUtil.getUserId(token);
        final String subject = jwtUtil.getSubject(token);
        final List<String> roles = jwtUtil.getRoles(token);

        final var authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        final CustomUserDetails userDetails =
                new CustomUserDetails(userId, subject, authorities);

        final UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        final String path = request.getRequestURI();
        final String method = request.getMethod();

        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }

        return WHITELIST.stream().anyMatch(path::startsWith);
    }
}
