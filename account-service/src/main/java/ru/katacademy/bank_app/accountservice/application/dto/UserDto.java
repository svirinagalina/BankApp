package ru.katacademy.bank_app.accountservice.application.dto;

import ru.katacademy.bank_app.accountservice.domain.enumtype.UserRole;

/**
 * DTO для передачи данных о пользователе между слоями приложения.
 * Используется для возврата информации о пользователе в presentation и application слоях.
 * <p>
 * Поля:
 * - id: идентификатор пользователя
 * - fullName: полное имя
 * - email: строковое представление email
 * - role: роль пользователя
 * <p>
 * Автор: Бачагов В.О.
 * Дата: 2025-04-15
 */
public record UserDto(
        Long id,
        String fullName,
        String email,
        UserRole role
) {
}