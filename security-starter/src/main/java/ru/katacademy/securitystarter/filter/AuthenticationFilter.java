package ru.katacademy.securitystarter.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
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
 * @since 2026-01-23
 */
public class AuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);

    private final UserIdentityResolver userIdentityResolver;
    private final SecurityContextRepository securityContextRepository;

    public AuthenticationFilter(UserIdentityResolver userIdentityResolver,
                               SecurityContextRepository securityContextRepository) {
        this.userIdentityResolver = userIdentityResolver;
        this.securityContextRepository = securityContextRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final Long userId = userIdentityResolver.resolve(request);

        if (userId != null) {
            final UserPrincipal principal = new UserPrincipal(userId);
            final UserAuthentication authentication = new UserAuthentication(principal);

            final SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, request, response);

            log.debug("User authenticated: userId={}", userId);
        }

        filterChain.doFilter(request, response);
    }
}