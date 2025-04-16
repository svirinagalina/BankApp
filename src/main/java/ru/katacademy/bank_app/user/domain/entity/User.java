package ru.katacademy.bank_app.user.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import ru.katacademy.bank_app.user.domain.enumtype.UserRole;
import ru.katacademy.bank_app.shared.valueobject.Email;

import java.time.LocalDateTime;

/**
 * Представляет пользователя системы.
 * Содержит основные поля для идентификации и авторизации.
 * <p>
 * Поля:
 * - id: уникальный идентификатор
 * - role: роль пользователя (USER, ADMIN)
 * - fullName: полное имя пользователя
 * - email: email как value object с валидацией
 * - passwordHash: хеш пароля
 * - createdAt: Дата и время регистрации
 * <p>
 * Автор: Бачагов В.О.
 * Дата: 2025-04-14
 */
@Getter
@RequiredArgsConstructor
public class User {
    private final Long id;
    private final UserRole role;
    private final String fullName;
    private final Email email;
    private final String passwordHash;
    private final LocalDateTime createdAt;

    /**
     * Проверяет, является ли пользователь администратором
     *
     * @return true если роль ADMIN
     */
    public boolean isAdmin() {
        return this.role == UserRole.ADMIN;
    }

    /**
     * Проверяет, является ли пользователь обычным пользователем
     *
     * @return true если роль USER
     */
    public boolean isUser() {
        return this.role == UserRole.USER;
    }
}