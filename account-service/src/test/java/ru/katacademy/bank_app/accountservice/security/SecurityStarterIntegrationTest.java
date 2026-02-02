package ru.katacademy.bank_app.accountservice.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("security-test")
@DisplayName("Security Starter Integration Tests")
class SecurityStarterIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should populate SecurityContext when X-User-Id header present")
    void shouldPopulateSecurityContextWithValidHeader() throws Exception {
        mockMvc.perform(get("/api/v1/accounts/me")
                        .header("X-User-Id", "12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(12345))
                .andExpect(jsonPath("$.authenticated").value(true));
    }

    @Test
    @DisplayName("Should return 401 when X-User-Id header missing")
    void shouldReturn401WhenHeaderMissing() throws Exception {
        mockMvc.perform(get("/api/v1/accounts/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Not authenticated"));
    }

    @Test
    @DisplayName("Should return 401 when X-User-Id is invalid")
    void shouldReturn401WhenHeaderInvalid() throws Exception {
        mockMvc.perform(get("/api/v1/accounts/me")
                        .header("X-User-Id", "invalid"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should allow public endpoint without authentication")
    void shouldAllowPublicEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/accounts/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}