package ru.katacademy.bank_app.accountservice.presentation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import ru.katacademy.bank_app.accountservice.application.dto.RegisterUserCommand;
import ru.katacademy.bank_app.accountservice.domain.service.UserService;
import ru.katacademy.bank_app.accountservice.infrastructure.aspect.ValidationAspect;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import({ValidationAspect.class, UserControllerAspectTest.MockConfig.class})
class UserControllerAspectTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }
    }

    @Test
    void whenValidInput_thenReturns200() throws Exception {
        final RegisterUserCommand validCommand = new RegisterUserCommand("Ivan", "ivan@mail.com", "securePassword");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCommand)))
                .andExpect(status().isOk());
    }

    @Test
    void whenInvalidInput_thenReturns400AndErrors() throws Exception {
        // 1. Подготовка невалидных данных
        final RegisterUserCommand invalidCommand = new RegisterUserCommand("IVAN", "invalid-email", "DADAD");

        // 2. Выполнение запроса с подробным логированием
        final MvcResult result = mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCommand)))
                .andDo(print()) // Важно для отладки!
                .andReturn();

        // 3. Проверка статуса вручную
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        // 4. Проверка наличия тела ответа
        final String content = result.getResponse().getContentAsString();
        assertThat(content).isNotBlank();

        // 5. Проверка формата JSON
        assertThatNoException().isThrownBy(() ->
                objectMapper.readTree(content)
        );

        // 6. Проверка структуры ответа
        final JsonNode json = objectMapper.readTree(content);
        assertThat(json.has("message")).isTrue();
        assertThat(json.get("message").asText()).contains("Имя не может быть пустым");
    }
}
