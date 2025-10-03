package ru.katacademy.bank_app.accountservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.katacademy.bank_app.accountservice.application.dto.RegisterUserCommand;
import ru.katacademy.bank_app.accountservice.application.dto.UserDto;
import ru.katacademy.bank_app.accountservice.domain.enumtype.UserRole;
import ru.katacademy.bank_app.accountservice.domain.service.UserService;
import ru.katacademy.bank_shared.exception.EmailAlreadyTakenException;
import ru.katacademy.bank_shared.exception.GlobalExceptionHandler;
import ru.katacademy.bank_shared.exception.UserNotFoundException;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Тесты для контроллера UserController
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import({GlobalExceptionHandler.class})
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * POST /api/users/register — успешная регистрация пользователя
     */
    @Test
    void registerUser_shouldReturn201() throws Exception {
        final RegisterUserCommand command = new RegisterUserCommand("John", "user@example.com", "123");
        final UserDto userDto = new UserDto(1L, "John", "user@example.com", UserRole.USER);

        given(userService.register(command)).willReturn(userDto);

        mockMvc.perform(post("/api/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.fullName").value("John"));
    }

    /**
     * POST /api/users/register — некорректный формат email → 400 Bad Request
     */
    @Test
    void registerUser_shouldReturn400WhenEmailFormatIsInvalid() throws Exception {
        final RegisterUserCommand command = new RegisterUserCommand("John", "invalid-email-format", "123");

        mockMvc.perform(post("/api/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());
    }

    /**
     * POST /api/users/register — email уже зарегистрирован → 409 Conflict
     */
    @Test
    void registerUser_shouldReturn409WhenEmailAlreadyExists() throws Exception {
        final RegisterUserCommand command = new RegisterUserCommand("John", "duplicate@example.com", "Password123");

        given(userService.register(command)).willThrow(new EmailAlreadyTakenException("duplicate@example.com"));

        mockMvc.perform(post("/api/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Email уже занят: duplicate@example.com"))
                .andExpect(jsonPath("$.status").value(409));
    }

    /**
     * GET /api/users/{id} — пользователь найден (владелец) → 200 OK
     */
    @Test
    @WithMockUser(username = "1", authorities = {"ROLE_USER"})
    void getById_shouldReturn200WhenUserExistsAndOwner() throws Exception {
        final long userId = 1L;
        final UserDto userDto = new UserDto(userId, "John", "john@example.com", UserRole.USER);
        given(userService.getById(userId)).willReturn(userDto);

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.fullName").value("John"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    /**
     * GET /api/users/{id} — пользователь не найден → 404 Not Found
     */
    @Test
    @WithMockUser(username = "1", authorities = {"ROLE_USER"})
    void getById_shouldReturn404WhenUserNotFound() throws Exception {
        final Long userId = 1L;
        given(userService.getById(userId)).willThrow(new UserNotFoundException(String.valueOf(userId)));

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isNotFound());
    }

    /**
     * GET /api/users/{id} — доступ запрещен (не владелец и не админ) → 403 Forbidden
     */
    @Test
    @WithMockUser(username = "1", authorities = {"ROLE_USER"})
    void getById_shouldReturn403WhenNotOwnerOrAdmin() throws Exception {
        final Long userId = 2L;

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isForbidden());
    }

    /**
     * GET /api/users/{id} — доступ разрешен для админа → 200 OK
     */
    @Test
    @WithMockUser(username = "1", authorities = {"ROLE_ADMIN"})
    void getById_shouldReturn200WhenAdminAccess() throws Exception {
        final long userId = 2L;
        final UserDto userDto = new UserDto(userId, "Jane", "jane@example.com", UserRole.USER);
        given(userService.getById(userId)).willReturn(userDto);

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.fullName").value("Jane"));
    }

    /**
     * GET /api/users/{id} — без аутентификации → 401 Unauthorized
     */
    @Test
    void getById_shouldReturn401WhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/users/{id}", 1L))
                .andExpect(status().isUnauthorized());
    }
}