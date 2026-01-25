package ru.katacademy.securitystarter.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.katacademy.securitystarter.auth.UserAuthentication;
import ru.katacademy.securitystarter.auth.UserPrincipal;
import ru.katacademy.securitystarter.identity.UserIdentityResolver;

import java.io.IOException;

/**
 * Фильтр аутентификации для установки пользовательского контекста безопасности.
 *
 * Выполняется один раз для каждого HTTP-запроса. Извлекает userId через
 * UserIdentityResolver и устанавливает Authentication в SecurityContext.
 *
 * Если userId не найден, запрос проходит без аутентификации.
 *
 * @author Galina
 * @date 2026-01-23
 */
@Slf4j
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

    private final UserIdentityResolver userIdentityResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

     final Long userId = userIdentityResolver.resolve(request);

        if (userId != null) {
        final UserPrincipal principal = new UserPrincipal(userId);
        final UserAuthentication authentication = new UserAuthentication(principal);

        SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("User authenticated: userId={}", userId);
        }

        filterChain.doFilter(request, response);
    }
}