package ru.katacademy.bank_app.accountservice.application.dto;

import java.time.LocalDateTime;

public class PasswordUpdatedEventV2 {
    private Long userId;
    private LocalDateTime timestamp;

    public PasswordUpdatedEventV2() {
        this.timestamp = LocalDateTime.now();
    }

    public PasswordUpdatedEventV2(Long userId) {
        this();
        this.userId = userId;
    }
}
