package ru.katacademy.securityservice.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Юнит‑тесты для проверки правил SecurityConfig.
 * <ul>
 *   <li>401 Unauthorized — без токена к защищённому URL.</li>
 *   <li>200 OK — с токеном (WithMockUser) к защищённому URL.</li>
 *   <li>401 Unauthorized — POST без CSRF (stateless).</li>
 *   <li>404 Not Found — публичный POST /api/users/register (разрешён, но контроллера нет).</li>
 * </ul>
 */
@SpringBootTest(properties = "spring.main.allow-bean-definition-overriding=true")
@AutoConfigureMockMvc
@Import({SecurityConfig.class, TestCorsConfig.class})
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * «Dummy» контроллер, который обслуживает /any/protected,
     * чтобы MockMvc мог дойти до конца цепочки фильтров.
     */
    @TestConfiguration
    @RestController
    static class DummyController {
        @GetMapping("/any/protected")
        public ResponseEntity<String> ok() {
            return ResponseEntity.ok("ok");
        }
    }

    @Test
    @DisplayName("Без токена любой защищённый URL даёт 401")
    void protectedEndpointRequiresAuth() throws Exception {
        mockMvc.perform(get("/any/protected"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("С токеном (WithMockUser) тот же URL даёт 200")
    @WithMockUser
    void protectedEndpointWithAuth() throws Exception {
        mockMvc.perform(get("/any/protected"))
                .andExpect(status().isOk())
                .andExpect(content().string("ok"));
    }

    @Test
    @DisplayName("POST без CSRF даёт 401 (stateless)")
    void postWithoutCsrf() throws Exception {
        mockMvc.perform(post("/any/protected")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Публичный POST /api/users/register — не 401/403")
    void publicRegisterAllowed() throws Exception {
        mockMvc.perform(post("/api/users/register")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"email\":\"a@b.com\",\"password\":\"pass\"}"))
                .andExpect(status().isNotFound());
    }
}
