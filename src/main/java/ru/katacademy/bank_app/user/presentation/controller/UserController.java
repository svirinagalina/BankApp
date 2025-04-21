package ru.katacademy.bank_app.user.presentation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import ru.katacademy.bank_app.user.application.dto.RegisterUserCommand;
import ru.katacademy.bank_app.user.application.dto.UserDto;
import ru.katacademy.bank_app.user.application.service.UserService;
import ru.katacademy.bank_app.shared.exception.UserNotFoundException;

import javax.validation.Valid;

/**
 * Контроллер для работы с пользователями.
 * Делегирует логику работы с пользователями в сервис UserService.
 * <p>
 * Методы:
 * - POST /api/users/register: регистрирует нового пользователя.
 * - GET /api/users/{id}: получает пользователя по ID.
 * <p>
 * Автор: Бачагов В.О.
 * Дата: 2025-04-18
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Регистрирует нового пользователя.
     *
     * @param cmd команда с данными для регистрации
     * @return ResponseEntity с DTO зарегистрированного пользователя
     */
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterUserCommand cmd) {
        UserDto userDto = userService.register(cmd);
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

    /**
     * Получает пользователя по ID.
     *
     * @param id идентификатор пользователя
     * @return ResponseEntity с DTO пользователя
     * @throws UserNotFoundException если пользователь не найден
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable Long id) {
        UserDto userDto = userService.getById(id);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
}