package ru.katacademy.bank_app.user.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import ru.katacademy.bank_app.user.domain.enumtype.UserRole;
import ru.katacademy.bank_app.user.domain.valueobject.Email;

import java.time.LocalDateTime;

/**
 * Представляет пользователя системы.
 * Содержит основные поля для идентификации и авторизации.
 * <p>
 * Поля:
 * - id: уникальный идентификатор
 * - role: роль пользователя (USER, ADMIN)
 * - fullName: полное имя пользователя
 * - email: Value Object с валидацией email
 * - passwordHash: хеш пароля
 * - createdAt: Дата и время регистрации
 * <p>
 * Автор: Бачагов В.О.
 * Дата: 2025-04-14
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private Email email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

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