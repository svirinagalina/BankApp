package ru.katacademy.bank_app.user.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.katacademy.bank_app.shared.valueobject.Email;
import ru.katacademy.bank_app.user.domain.enumtype.UserRole;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Table(name = "users")
public class UserEntity {
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
}
