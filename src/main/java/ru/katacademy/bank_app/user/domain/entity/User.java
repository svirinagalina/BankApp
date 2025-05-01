package ru.katacademy.bank_app.user.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import ru.katacademy.bank_app.user.domain.enumtype.UserRole;
import ru.katacademy.bank_app.shared.valueobject.Email;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Представляет пользователя системы.
 */
@Entity
@Table(name = "users")
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private final UserRole role;

    @Column(name = "full_name", nullable = false)
    private final String fullName;

    @Embedded
    private final Email email;

    @Column(name = "password_hash", nullable = false)
    private final String passwordHash;

    @Column(name = "created_at", nullable = false, updatable = false)
    private final LocalDateTime createdAt;

    // JPA требует конструктор без аргументов
    protected User() {
        this.id = null;
        this.role = null;
        this.fullName = null;
        this.email = null;
        this.passwordHash = null;
        this.createdAt = null;
    }

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

    // Остальные методы остаются без изменений
    public boolean isAdmin() {
        return this.role == UserRole.ADMIN;
    }

    public boolean isUser() {
        return this.role == UserRole.USER;
    }
}