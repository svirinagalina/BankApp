package ru.katacademy.bank_app.accountservice.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;
import ru.katacademy.securitystarter.filter.AuthenticationFilter;
import ru.katacademy.securitystarter.identity.UserIdentityResolver;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("security-test")
@DisplayName("Security Beans Configuration Test")
class SecurityBeansTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    @DisplayName("Should create AccountUserIdentityResolver bean")
    void shouldCreateAccountUserIdentityResolverBean() {
        final UserIdentityResolver resolver = applicationContext.getBean(UserIdentityResolver.class);

        assertThat(resolver).isNotNull();
        assertThat(resolver).isInstanceOf(AccountUserIdentityResolver.class);
    }

    @Test
    @DisplayName("Should create AuthenticationFilter bean")
    void shouldCreateAuthenticationFilterBean() {
        final AuthenticationFilter filter = applicationContext.getBean(AuthenticationFilter.class);

        assertThat(filter).isNotNull();
    }

    @Test
    @DisplayName("Should create SecurityFilterChain bean")
    void shouldCreateSecurityFilterChainBean() {
        final SecurityFilterChain securityFilterChain =
            applicationContext.getBean(SecurityFilterChain.class);

        assertThat(securityFilterChain).isNotNull();
    }
}
