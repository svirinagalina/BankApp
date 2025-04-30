package ru.katacademy.bank_app.user.domain.entity;

import lombok.Data;
import ru.katacademy.bank_app.user.domain.enumtype.UserRole;
import ru.katacademy.bank_app.shared.valueobject.Email;

import java.time.LocalDateTime;
import java.util.Objects;

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
@Data
public class User {
    private final Long id;
    private final UserRole role;
    private final String fullName;
    private final Email email;
    private String passwordHash;
    private final LocalDateTime createdAt;

    public User(Long id,
                UserRole role,
                String fullName,
                Email email,
                String passwordHash,
                LocalDateTime createdAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.role = Objects.requireNonNull(role, "role must not be null");
        this.fullName = Objects.requireNonNull(fullName, "fullName must not be null");
        this.email = Objects.requireNonNull(email, "email must not be null");
        this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
    }

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