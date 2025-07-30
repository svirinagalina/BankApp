package ru.katacademy.auth.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.katacademy.auth.domain.entity.LoginAttempt;
import ru.katacademy.auth.infrastructure.mapper.LoginAttemptMapper;

import java.time.LocalDateTime;

/**
 * JPA-сущность, представляющая попытку входа в систему.
 * <p>
 * Соответствует таблице {@code auth_login_attempts} в базе данных.
 * Содержит аннотации JPA для маппинга полей сущности на колонки таблицы.
 * Используется только в инфраструктурном слое для работы с базой данных.
 * Соответствует domain-объекту:
 * Преобразование в/из {@link LoginAttempt} осуществляется через {@link LoginAttemptMapper}
 * </p>
 *
 * @author MihasBatler
 * @see LoginAttempt Соответствующая domain-сущность
 */
@Entity
@Table(name = "auth_login_attempts")
@Getter
@Setter
@NoArgsConstructor
public class LoginAttemptEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "success", nullable = false)
    private boolean success;

    @Column(name = "ip_address")
    private String ip;

    @Column(name = "user_agent")
    private String userAgent;
}
