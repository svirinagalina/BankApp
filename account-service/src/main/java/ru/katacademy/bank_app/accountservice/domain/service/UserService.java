package ru.katacademy.bank_app.accountservice.domain.service;


import ru.katacademy.bank_app.accountservice.application.command.ChangePasswordCommand;
import ru.katacademy.bank_app.accountservice.application.dto.RegisterUserCommand;
import ru.katacademy.bank_app.accountservice.application.dto.UserDto;

/**
 * Сервис для работы с пользователями.
 * Содержит бизнес-логику регистрации и получения пользователей.
 * <p>
 * Методы:
 * - register(): регистрирует нового пользователя
 * - getById(): возвращает пользователя по ID
 * <p>
 * Автор: Бачагов В.О.
 * Дата: 2025-04-17
 */
public interface UserService {

    /**
     * Регистрирует нового пользователя.
     * Валидирует данные, проверяет уникальность email, сохраняет нового пользователя.
     *
     * @param cmd команда с данными для регистрации
     * @return DTO зарегистрированного пользователя
     */
    UserDto register(RegisterUserCommand cmd);

    /**
     * Получает пользователя по ID.
     * Если пользователь не найден, выбрасывает исключение.
     *
     * @param id идентификатор пользователя
     * @return DTO пользователя
     */
    UserDto getById(Long id);

    void changePassword(ChangePasswordCommand command);

}
