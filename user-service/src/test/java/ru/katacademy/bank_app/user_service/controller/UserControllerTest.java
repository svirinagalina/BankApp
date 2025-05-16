package ru.katacademy.bank_app.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.katacademy.bank_app.user_service.application.dto.RegisterUserCommand;
import ru.katacademy.bank_app.user_service.application.dto.UserDto;
import ru.katacademy.bank_app.user_service.domain.enumtype.UserRole;
import ru.katacademy.bank_app.user_service.domain.service.UserService;
import ru.katacademy.bank_app.user_service.presentation.controller.UserController;
import ru.katacademy.bank_shared.exception.GlobalExceptionHandler;
import ru.katacademy.bank_shared.exception.UserNotFoundException;


import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Тесты для контроллера UserController: проверяется регистрация и получение пользователя по ID.
 */
@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal")
    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * POST /api/users/register — успешная регистрация пользователя
     */
    @Test
    void registerUser_shouldReturn201() throws Exception {
        RegisterUserCommand command = new RegisterUserCommand("John", "user@example.com", "123");
        UserDto userDto = new UserDto(1L, "John", "user@example.com", UserRole.USER);

        given(userService.register(command)).willReturn(userDto);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command))) // Использование ObjectMapper
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.fullName").value("John"));
    }

    /**
     * POST /api/users/register — некорректный формат email → 400 Bad Request
     */
    @Test
    void registerUser_shouldReturn400WhenEmailFormatIsInvalid() throws Exception {
        RegisterUserCommand command = new RegisterUserCommand("invalid-email-format", "John", "123");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());
    }

    /**
     * GET /api/users/{id} — пользователь найден → 200 OK
     */
    @Test
    void getById_shouldReturn200WhenUserExists() throws Exception {
        long userId = 1L;
        UserDto userDto = new UserDto(userId, "John", "john@example.com", UserRole.USER);
        given(userService.getById(userId)).willReturn(userDto);

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.fullName").value("John"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    /**
     * GET /api/users/{id} — пользователь не найден → 404 Not Found
     */
    @Test
    void getById_shouldReturn404WhenUserNotFound() throws Exception {
        long userId = 999L;
        given(userService.getById(userId)).willThrow(new UserNotFoundException(String.valueOf(userId)));

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isNotFound());
    }
}

