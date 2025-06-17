package ru.katacademy.bank_app.accountservice.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.katacademy.bank_app.accountservice.domain.enumtype.UserRole;
import ru.katacademy.bank_shared.conventor.EmailAttributeConverter;
import ru.katacademy.bank_shared.valueobject.Email;

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

    @Convert(converter = EmailAttributeConverter.class)
    @Column(name="email", nullable=false, unique=true)
    private final Email email;

    @Column(name = "password_hash", nullable = false)
    private final String passwordHash;

    @Column(name = "created_at", nullable = false, updatable = false)
    private final LocalDateTime createdAt;
}
