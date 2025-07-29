package ru.katacademy.bank_app.accountservice.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * JPA‑сущность для таблицы «login_attempts».
 * Соответствует доменному объекту {@link ru.katacademy.bank_app.accountservice.domain.entity.LoginAttemptEntry}.
 */
@Entity
@Table(name = "login_attempts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class LoginAttemptEntryEntity {

    /** Идентификатор записи в БД. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Идентификатор пользователя, сделавшего попытку. */
    private Long userId;

    /** Email пользователя на момент попытки. */
    private String email;

    /** IP‑адрес клиента. */
    private String ip;

    /** User‑Agent клиента. */
    private String userAgent;

    /** Временная метка попытки. */
    private LocalDateTime timestamp;

    /** Флаг успешности попытки. */
    private boolean success;
}
