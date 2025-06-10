package ru.katacademy.bank_app.accountservice.domain.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoginAttemptedEvent {
    private Long userId;
    private String ip;
    private String userAgent;
    private LocalDateTime timestamp;
    private boolean success;
}
