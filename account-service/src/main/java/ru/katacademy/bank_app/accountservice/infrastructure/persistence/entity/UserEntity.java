package ru.katacademy.bank_app.accountservice.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.katacademy.bank_app.accountservice.domain.entity.User;
import ru.katacademy.bank_app.accountservice.domain.enumtype.UserRole;
import ru.katacademy.bank_shared.conventor.EmailAttributeConverter;
import ru.katacademy.bank_shared.valueobject.Email;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA‑сущность для таблицы «users».
 * Соответствует доменному объекту {@link User}.
 */
@Entity
@Data
@NoArgsConstructor(force = true)
@Table(name = "users")
public class UserEntity {

    /** Идентификатор записи в БД. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;

    /** Роль пользователя. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private final UserRole role;

    /** Полное имя пользователя. */
    @Column(name = "full_name", nullable = false)
    private final String fullName;

    /** Email пользователя. */
    @Convert(converter = EmailAttributeConverter.class)
    @Column(name="email", nullable=false, unique=true)
    private final Email email;

    /** Пароль пользователя. */
    @Column(name = "password_hash", nullable = false)
    private final String passwordHash;

    /** Время создания пользователя. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private final LocalDateTime createdAt;

    /** Банковские аккаунты пользователя. */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<AccountEntity> accounts = new ArrayList<>();

    public UserEntity(Long id, UserRole role, String fullName, Email email, String passwordHash, LocalDateTime createdAt) {
        this.id = id;
        this.role = role;
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }
}
