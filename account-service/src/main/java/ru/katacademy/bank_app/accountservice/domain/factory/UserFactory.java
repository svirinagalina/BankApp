package ru.katacademy.bank_app.accountservice.domain.factory;

import ru.katacademy.bank_shared.valueobject.Email;
import ru.katacademy.bank_app.accountservice.application.dto.RegisterUserCommand;
import ru.katacademy.bank_app.accountservice.domain.entity.User;
import ru.katacademy.bank_app.accountservice.domain.enumtype.UserRole;

import java.time.LocalDateTime;

/**
 * Фабрика для создания экземпляров {@link User} из команд и DTO.
 * <p>
 * Инкапсулирует логику валидации и преобразования входных данных в доменные сущности.
 * </p>
 * Автор: Бачагов В.О.
 * Дата: 2025-04-18
 */
public class UserFactory {
    /**
     * Создает нового пользователя из команды регистрации.
     *
     * @param cmd команда с данными для регистрации
     * @return новый пользователь
     */
    public static User create(RegisterUserCommand cmd) {
        return new User(
                UserRole.USER, // Роль по умолчанию
                cmd.fullName(),
                new Email(cmd.email()),
                cmd.password(),
                LocalDateTime.now());
    }
}