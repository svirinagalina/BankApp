package ru.katacademy.bank_app.accountservice.domain.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Класс для представления записи о попытке входа пользователя в систему.
 * <p>
 * Этот класс содержит информацию о попытке входа, такую как идентификатор пользователя,
 * электронная почта, IP-адрес, информация о пользовательском агенте,
 * временная метка и статус успешной попытки.
 * </p>
 *
 * Автор: Колпаков А.С..
 * Дата: 2025-05-05
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class LoginAttemptEntry {
    private Long id;

    private Long userId;

    private String email;

    private String ip;

    private String userAgent;

    private LocalDateTime timestamp;

    private boolean success;

    public LoginAttemptEntry(Long userId, String email, String ip, String userAgent, LocalDateTime timestamp, boolean success) {
        this.userId = userId;
        this.email = email;
        this.ip = ip;
        this.userAgent = userAgent;
        this.timestamp = timestamp;
        this.success = success;
    }
}