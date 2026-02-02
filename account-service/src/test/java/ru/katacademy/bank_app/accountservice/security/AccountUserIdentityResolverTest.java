package ru.katacademy.bank_app.accountservice.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AccountUserIdentityResolver Tests")
class AccountUserIdentityResolverTest {

    private final AccountUserIdentityResolver resolver = new AccountUserIdentityResolver();

    @Test
    @DisplayName("Should resolve userId from valid X-User-Id header")
    void shouldResolveUserIdFromValidHeader() {

        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-User-Id", "12345");

        final Long userId = resolver.resolve(request);

        assertThat(userId).isEqualTo(12345L);
    }

    @Test
    @DisplayName("Should return null when X-User-Id header is missing")
    void shouldReturnNullWhenHeaderMissing() {
        final MockHttpServletRequest request = new MockHttpServletRequest();

        final Long userId = resolver.resolve(request);

        assertThat(userId).isNull();
    }

    @Test
    @DisplayName("Should return null when X-User-Id header is blank")
    void shouldReturnNullWhenHeaderBlank() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-User-Id", "   ");

        final Long userId = resolver.resolve(request);


        assertThat(userId).isNull();
    }

    @Test
    @DisplayName("Should return null when X-User-Id is not a valid number")
    void shouldReturnNullWhenHeaderInvalid() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-User-Id", "not-a-number");

        final Long userId = resolver.resolve(request);


        assertThat(userId).isNull();
    }

    @Test
    @DisplayName("Should handle negative userId")
    void shouldHandleNegativeUserId() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-User-Id", "-999");

        final Long userId = resolver.resolve(request);

        assertThat(userId).isEqualTo(-999L);
    }

    @Test
    @DisplayName("Should handle large userId")
    void shouldHandleLargeUserId() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-User-Id", String.valueOf(Long.MAX_VALUE));

        final Long userId = resolver.resolve(request);

        assertThat(userId).isEqualTo(Long.MAX_VALUE);
    }
}