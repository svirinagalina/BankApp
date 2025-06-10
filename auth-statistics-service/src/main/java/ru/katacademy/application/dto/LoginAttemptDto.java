package ru.katacademy.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginAttemptDto {
    private Long userId;
    private String ip;
    private String userAgent;
    private LocalDateTime timestamp;
    private boolean success;
}
