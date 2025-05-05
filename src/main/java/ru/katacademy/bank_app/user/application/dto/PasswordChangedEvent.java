package ru.katacademy.bank_app.user.application.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class PasswordChangedEvent {
    private Long userId;
    private LocalDateTime timestamp;
    private String oldPassword;
    private String newPassword;

    public PasswordChangedEvent() {
        this.timestamp = LocalDateTime.now();
    }

    public PasswordChangedEvent(Long userId, String oldPassword, String newPassword) {
        this();
        this.userId = userId;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }
}
