package ru.katacademy.securitystarter.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ru.katacademy.securitystarter.identity.UserIdentity;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Реализация Spring Security Authentication.
 * <p>
 * Хранит информацию о субъекте запроса (UserIdentity) — пользователь или сервис.
 * Всегда считается аутентифицированным (isAuthenticated = true).
 *
 * @author Galina
 * @date 2026-01-23
 */
public class UserAuthentication implements Authentication {

    private final UserIdentity identity;
    private boolean authenticated = true;

    public UserAuthentication(UserIdentity identity) {
        this.identity = identity;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return identity.roles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return identity;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        if (identity.userId() != null) {
            return identity.userId().toString();
        }
        // Для сервисов — берём имя из attributes
        final Object serviceName = identity.attributes().get("serviceName");
        if (serviceName != null) {
            return serviceName.toString();
        }
        return "unknown";
    }
}