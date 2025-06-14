package ru.katacademy.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Класс, представляющий попытку входа пользователя в систему.
 * <p>
 * Содержит информацию о пользователе.
 * Данные сохраняются в таблице БД {@code auth_login_attempts}.
 * </p>
 *
 * @author MihasBatler
 */
@Entity
@Table(name = "auth_login_attempts")
@NoArgsConstructor
@Setter
@Getter
public class LoginAttempt {

    /**
     * Уникальный идентификатор записи в базе данных.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Идентификатор пользователя.
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * Дата и время попытки входа.
     */
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    /**
     * Результат попытки входа(true/false).
     */
    @Column(name = "success", nullable = false)
    private boolean success;

    /**
     * IP-адрес, с которого выполнена попытка входа.
     */
    @Column(name = "ip_address")
    private String ip;

    /**
     * Информация о браузере/устройстве пользователя.
     */
    @Column(name = "user_agent")
    private String userAgent;
}
