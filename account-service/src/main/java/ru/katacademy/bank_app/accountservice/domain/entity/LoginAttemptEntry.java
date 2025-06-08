package ru.katacademy.bank_app.accountservice.domain.entity;

import jakarta.persistence.*;
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
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "login_attempts")
public class LoginAttemptEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String ip;

    @Column(name = "user_agent", length = 512)
    private String userAgent;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
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