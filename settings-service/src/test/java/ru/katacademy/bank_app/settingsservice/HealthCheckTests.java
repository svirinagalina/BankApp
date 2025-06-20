package ru.katacademy.bank_app.settingsservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Интеграционные тесты проверки состояния здоровья (health) приложения через Spring Boot Actuator.
 *
 * <p>Тесты запускаются с реальным контекстом приложения на случайном порту.
 * Активен профиль {@code test}.</p>
 *
 * <p>Проверяется, что endpoint {@code /actuator/health} доступен и возвращает статус {@code UP}.</p>
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "management.endpoint.health.show-details=always",
                "management.endpoints.web.exposure.include=health",
                "management.health.defaults.enabled=false",
                "management.endpoint.health.validate-group-membership=false"
        }
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class HealthCheckTests {

    /**
     * MockMvc для имитации HTTP-запросов к REST API приложения.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Тест проверяет, что вызов {@code GET /actuator/health} возвращает HTTP 200 и
     * JSON с полем {@code status} равным {@code UP}.
     *
     * @throws Exception при ошибках выполнения запроса
     */
    @Test
    void healthShouldBeUp() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}