package ru.katacademy.bank_shared.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String username;
    private final List<GrantedAuthority> authorities; // concrete list, unmodifiable

    public CustomUserDetails(Long id, String username, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        // null-safe copy + make unmodifiable to avoid exposing internal state
        final Collection<? extends GrantedAuthority> incoming = authorities == null
                ? Collections.emptyList()
                : authorities;
        this.authorities = Collections.unmodifiableList(new ArrayList<>(incoming));
    }

    @Override
    public String getUsername() {
        return username;
    }

    // возвращаем уже безопасную (немодифицируемую) коллекцию
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null; // пароль не хранится в JWT-based UserDetails
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}