package ru.katacademy.securityservice;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.katacademy.securityservice.config.SecurityConfig;
import ru.katacademy.securityservice.util.JwtUtil;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = JwtAuthenticationFilterTest.SecuredController.class)
@Import(SecurityConfig.class)
public class JwtAuthenticationFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @RestController
    static class SecuredController {
        @GetMapping("/secured-endpoint")
        public String securedEndpoint() {
            return "ok";
        }
    }

    @Test
    void whenInvalidToken_thenUnauthorized() throws Exception {

        //вставляем токен, который вызовет исключение
        final String invalidToken = "invalid.jwt.token";
        Mockito.when(jwtUtil.getSubject(invalidToken)).thenThrow(new JwtException("Invalid token"));

        // проверяем, что фильтр вернет 401
        mockMvc.perform(get("/secured-endpoint")
                .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isUnauthorized());
    }
}
