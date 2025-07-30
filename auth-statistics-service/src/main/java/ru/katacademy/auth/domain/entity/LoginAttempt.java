package ru.katacademy.auth.domain.entity;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class LoginAttempt {

    /**
     * Уникальный идентификатор записи в базе данных.
     */
    private Long id;

    /**
     * Идентификатор пользователя.
     */
    private Long userId;

    /**
     * Дата и время попытки входа.
     */
    private LocalDateTime timestamp;

    /**
     * Результат попытки входа(true/false).
     */
    private boolean success;

    /**
     * IP-адрес, с которого выполнена попытка входа.
     */
    private String ip;

    /**
     * Информация о браузере/устройстве пользователя.
     */
    private String userAgent;
}
