package ru.katacademy.securityservice;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.katacademy.securityservice.util.JwtUtil;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class JwtAuthenticationFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void whenInvalidToken_thenUnauthorized() throws Exception {

        //вставляем токен, который вызовет исключение
        final String invalidToken = "invalid.jwt.token";
        Mockito.when(jwtUtil.getSubject(invalidToken)).thenThrow(new JwtException("Invalid token"));

        // проверяем, что фильтр вернет 401
        mockMvc.perform(get("/secured-endpoint")
                .header("Authorization", "Bearer" + invalidToken))
                .andExpect(status().isUnauthorized());
    }
}
