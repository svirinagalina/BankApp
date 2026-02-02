package ru.katacademy.securitystarter.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

/**
 * Реализация Spring Security Authentication для минимальной авторизации.
 * <p>
 * Хранит информацию о пользователе (UserPrincipal) без ролей и прав доступа.
 * Всегда считается аутентифицированным (isAuthenticated = true).
 *
 * @author Galina
 * @since 2026-01-23
 */
public class UserAuthentication implements Authentication {

    private final UserPrincipal principal;
    private boolean authenticated;

    private UserAuthentication(Builder builder) {
        this.principal = builder.principal;
        this.authenticated = builder.authenticated;
    }

    public UserAuthentication(UserPrincipal principal) {
        this.principal = principal;
        this.authenticated = true;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
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
        return principal;
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
        return principal.userId().toString();
    }

    public static class Builder {
        private UserPrincipal principal;
        private boolean authenticated = true;

        public Builder principal(UserPrincipal principal) {
            this.principal = principal;
            return this;
        }

        public Builder authenticated(boolean authenticated) {
            this.authenticated = authenticated;
            return this;
        }

        public UserAuthentication build() {
            return new UserAuthentication(this);
        }
    }
}