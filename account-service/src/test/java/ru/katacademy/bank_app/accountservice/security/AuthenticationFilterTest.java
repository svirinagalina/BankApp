package ru.katacademy.bank_app.accountservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.katacademy.securitystarter.auth.UserPrincipal;
import ru.katacademy.securitystarter.filter.AuthenticationFilter;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DisplayName("AuthenticationFilter Direct Tests")
class AuthenticationFilterTest {

    private final AccountUserIdentityResolver resolver = new AccountUserIdentityResolver();
    private final AuthenticationFilter filter = new AuthenticationFilter(resolver);

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should set Authentication in SecurityContext when userId present")
    void shouldSetAuthenticationWhenUserIdPresent() throws ServletException, IOException {
        // Given
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-User-Id", "12345");
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final FilterChain filterChain = mock(FilterChain.class);

        filter.doFilter(request, response, filterChain);

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.isAuthenticated()).isTrue();

        final UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        assertThat(principal.userId()).isEqualTo(12345L);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should not set Authentication when userId missing")
    void shouldNotSetAuthenticationWhenUserIdMissing() throws ServletException, IOException {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final FilterChain filterChain = mock(FilterChain.class);

        filter.doFilter(request, response, filterChain);

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNull();

        verify(filterChain).doFilter(request, response);
    }
}
