package ru.katacademy.bank_app.accountservice.presentation.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.katacademy.bank_app.accountservice.application.dto.RegisterUserCommand;
import ru.katacademy.bank_app.accountservice.domain.service.UserService;
import ru.katacademy.bank_app.accountservice.infrastructure.aop.ValidationAspect;
import ru.katacademy.bank_shared.exception.GlobalExceptionHandler;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тестовый класс для {@link UserController}.
 * Этот класс выполняет тестирование контроллера UserController с использованием мок-сервиса для UserService.
 * Он тестирует сценарии с валидными и невалидными данными, проверяя, что контроллер правильно обрабатывает запросы.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class UserControllerAspectTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Вспомогательный конфигурационный класс, который создает мок для {@link UserService}.
     */
    @TestConfiguration
    static class MockConfig {
        /**
         * Создает и возвращает мок-объект {@link UserService}.
         *
         * @return Мок-объект {@link UserService}.
         */
        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }
    }

    /**
     * Устанавливает системное свойство для кодировки перед каждым тестом.
     * Этот метод выполняется перед каждым тестом.
     */
    @BeforeEach
    public void setUp() {
        System.setProperty("file.encoding", "UTF-8");
    }

    /**
     * Тестирует успешный запрос на регистрацию пользователя с валидными данными.
     * Ожидается статус 201 Created.
     *
     * @throws Exception Исключение, если произошла ошибка выполнения запроса.
     */
    @Test
    void whenValidInput_thenReturns200() throws Exception {
        final RegisterUserCommand validCommand = new RegisterUserCommand("Andrey", "andrey@mail.com", "12345678");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCommand)))
                .andExpect(status().isCreated());
    }

    /**
     * Тестирует запрос на регистрацию пользователя с невалидными данными.
     * Ожидается статус 400 Bad Request и список ошибок в теле ответа.
     *
     * @throws Exception Исключение, если произошла ошибка выполнения запроса.
     */
    @Test
    void whenInvalidInput_thenReturns400AndErrors() throws Exception {

        final RegisterUserCommand invalidCommand = new RegisterUserCommand("", "andreymail.com", "");

        final MvcResult result = mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCommand)))
                .andDo(print())
                .andReturn();
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());

        final String content = result.getResponse().getContentAsString();
        assertThat(content).isNotBlank();

        assertThatNoException().isThrownBy(() -> objectMapper.readTree(content));

        final JsonNode json = objectMapper.readTree(content);
        assertThat(json.has("messages")).isTrue();
        assertThat(json.get("messages").isArray()).isTrue();

        final List<String> errorMessages = new ArrayList<>();
        json.get("messages").forEach(msgNode -> errorMessages.add(msgNode.asText()));

        assertThat(errorMessages).contains("Имя не может быть пустым");
        assertThat(errorMessages).contains("Некорректный email адрес");
        assertThat(errorMessages).contains("Пароль не может быть пустым");
    }
}
