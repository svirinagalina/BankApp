package ru.katacademy.auth.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Сущность для хранения статистики смен пароля пользователя.
 */
@Entity
@Table(name = "user_password_stats")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPasswordStats {

    @Id
    private Long userId;

    /**
     * Количество смен пароля пользователем
     */
    private Long passwordChangeCount;

    /**
     * Дата и время последней смены пароля
     */
    private Instant lastPasswordChange;
}