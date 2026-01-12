package ru.katacademy.bank_app.accountservice.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.katacademy.bank_app.accountservice.adapters.web.mapper.UserWebMapper;
import ru.katacademy.bank_app.accountservice.adapters.web.request.user.RegisterUserRequest;
import ru.katacademy.bank_app.accountservice.adapters.web.response.error.ErrorResponse;
import ru.katacademy.bank_app.accountservice.adapters.web.response.user.UserRegisteredResponse;
import ru.katacademy.bank_app.accountservice.application.dto.UserDto;
import ru.katacademy.bank_app.accountservice.domain.service.UserService;
import ru.katacademy.bank_shared.exception.EmailAlreadyTakenException;
import ru.katacademy.bank_shared.exception.InvalidEmailException;
import ru.katacademy.bank_shared.exception.UserNotFoundException;

/**
 * Контроллер для управления пользователями через REST API.
 *
 * <p>Методы:</p>
 * <ul>
 *     <li><b>POST /api/users/register</b> — регистрация нового пользователя</li>
 *     <li><b>GET /api/users/{id}</b> — получение пользователя по ID</li>
 * </ul>
 *
 * <p>Автор: Бачагов В.О.</p>
 * <p>Дата: 2025-04-18</p>
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Регистрирует нового пользователя.
     *
     * @param cmd команда с данными для регистрации
     * @return DTO зарегистрированного пользователя
     * @throws EmailAlreadyTakenException если email уже зарегистрирован
     * @throws InvalidEmailException      если Email не валидный
     */
    @Operation(summary = "Регистрация пользователя", description = "Создаёт нового пользователя с переданными данными.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = RegisterUserRequest.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "Пример запроса",
                                    value = """
                                            {
                                              "fullName": "James Gosling",
                                              "email": "jago@gmail.com",
                                              "password": "james1111"
                                            }
                                            """
                            ))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserRegisteredResponse.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации входных данных",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "Ошибка валидации",
                                    value = """
                    {
                      "timestamp": "2026-01-10 10:37:07",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Ошибка валидации входных данных",
                      "path": "/api/users/register",
                      "errors": [
                        "Email должен быть валидным",
                        "Пароль должен содержать минимум 8 символов"
                      ]
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Email уже зарегистрирован",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "Email занят",
                                    value = """
                    {
                      "timestamp": "2026-01-10 10:37:07",
                      "status": 409,
                      "error": "Conflict",
                      "message": "Email уже занят: user@example.com",
                      "path": "/api/users/register"
                    }
                    """
                            )
                    )
            )
    })
    @PostMapping("/register")
    public ResponseEntity<UserRegisteredResponse> register(@Valid @RequestBody RegisterUserRequest cmd) {
        final UserDto userDto = userService.register(cmd);
        final UserRegisteredResponse response = UserWebMapper.toUserRegisteredResponse(userDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Получает пользователя по ID.
     *
     * @param id идентификатор пользователя
     * @return DTO пользователя
     * @throws UserNotFoundException если пользователь не найден
     */
    @Operation(summary = "Получение пользователя по ID", description = "Возвращает информацию о пользователе по его ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь найден",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Пользователь не найден",
                                    value = """
                {
                  "timestamp": "2026-01-10 10:37:07",
                  "status": 404,
                  "error": "Not Found",
                  "message": "Пользователь с id 103 не найден",
                  "path": "/api/accounts"
                }
                """
                            )
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @PathVariable Long id,
            Authentication authentication) {

        if (authentication == null) {
            return handleUnauthenticatedRequest(id);
        }

        return handleAuthenticatedRequest(id, authentication);
    }

    private ResponseEntity<?> handleUnauthenticatedRequest(Long id) {
        try {
            final UserDto userDto = userService.getById(id);
            return ResponseEntity.ok(userDto);
        } catch (UserNotFoundException e) {
            return buildUserNotFoundErrorResponse(id);
        }
    }

    private ResponseEntity<?> handleAuthenticatedRequest(Long id, Authentication authentication) {
        try {
            final String username = authentication.getName();
            final Long currentUserId = Long.valueOf(username);

            if (!hasAccessToUser(id, currentUserId, authentication)) {
                return buildForbiddenErrorResponse(id);
            }

            final UserDto userDto = userService.getById(id);
            return ResponseEntity.ok(userDto);

        } catch (NumberFormatException e) {
            return buildInvalidUserIdErrorResponse(id);
        } catch (UserNotFoundException e) {
            return buildUserNotFoundErrorResponse(id);
        }
    }

    private boolean hasAccessToUser(Long requestedUserId, Long currentUserId, Authentication authentication) {
        final boolean isOwner = currentUserId.equals(requestedUserId);
        final boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        return isOwner || isAdmin;
    }

    private ResponseEntity<ErrorResponse> buildUserNotFoundErrorResponse(Long id) {
        final ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message("Пользователь с id " + id + " не найден")
                .path("/api/users/" + id)
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    private ResponseEntity<ErrorResponse> buildForbiddenErrorResponse(Long id) {
        final ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error("Forbidden")
                .message("Доступ запрещен")
                .path("/api/users/" + id)
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    private ResponseEntity<ErrorResponse> buildInvalidUserIdErrorResponse(Long id) {
        final ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error("Forbidden")
                .message("Неверный формат идентификатора пользователя")
                .path("/api/users/" + id)
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
}