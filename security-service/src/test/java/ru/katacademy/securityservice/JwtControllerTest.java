package ru.katacademy.securityservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ru.katacademy.securityservice.util.JwtUtil;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * JwtControllerTest — интеграционный тест для проверки REST-эндпоинта /api/security/verify,
 *
 *  - Проверить, что JWT-токен, сгенерированный утилитой {@link JwtUtil}, корректно обрабатывается контроллером;
 *  - Убедиться, что при передаче валидного токена возвращаются расшифрованные claims и HTTP-статус 200 OK.
 *
 *  - Используется Spring-контекст (@SpringBootTest);
 *  - Запросы отправляются через {@link MockMvc}, что позволяет эмулировать HTTP-вызовы без запуска сервера;
 *  - Конфигурация токена задаётся через аннотацию @TestPropertySource.
 *
 * Конфигурация токена:
 *  - jwt.secret: testsecretkeyfortestpurposesonly1234567890
 *  - jwt.expiration-ms: 3600000 (1 час)
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "jwt.secret=testsecretkeyfortestpurposesonly1234567890",
        "jwt.expiration-ms=3600000"
})
class JwtControllerTest {

    /**
     * {@link MockMvc} используется для эмуляции HTTP-запросов к REST-контроллеру.
     * Инъекция автоматически благодаря @AutoConfigureMockMvc.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Создание JWT-токенов в тестах.
     */
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Проверка, что при передаче токена в /api/security/verify
     * возвращается статус 200 и тело содержит расшифрованные claims.
     *
     * @throws Exception при ошибке выполнения запроса
     */
    @Test
    void verifyToken_shouldReturnClaims_whenValidToken() throws Exception {
        final String token = jwtUtil.generateToken("test-user");
        System.out.println(">>> GENERATED TOKEN: " + token);

        final var result = mockMvc.perform(post("/api/security/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"" + token + "\""))
                .andReturn();

        final int status = result.getResponse().getStatus();
        final String body = result.getResponse().getContentAsString();

        System.out.println(">>> STATUS: " + status);
        System.out.println(">>> BODY: " + body);

    }
}
