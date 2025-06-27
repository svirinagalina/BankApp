package ru.katacademy.bank_app.accountservice.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.katacademy.bank_app.accountservice.application.dto.RegisterUserCommand;
import ru.katacademy.bank_app.accountservice.application.dto.UserDto;
import ru.katacademy.bank_app.accountservice.domain.service.UserService;
import ru.katacademy.bank_shared.exception.EmailAlreadyTakenException;
import ru.katacademy.bank_shared.exception.UserNotFoundException;
import ru.katacademy.bank_shared.exception.InvalidEmailException;

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
     * @throws InvalidEmailException если Email не валидный
     */
    @Operation(summary = "Регистрация пользователя", description = "Создаёт нового пользователя с переданными данными.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Email уже зарегистрирован",
                    content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterUserCommand cmd) {
        final UserDto userDto = userService.register(cmd);
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
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
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable Long id) {
        final UserDto userDto = userService.getById(id);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
}