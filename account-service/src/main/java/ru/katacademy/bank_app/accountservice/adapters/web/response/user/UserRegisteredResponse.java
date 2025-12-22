package ru.katacademy.bank_app.accountservice.adapters.web.response.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Ответ после успешной регистрации пользователя.
 * Возвращается со статусом 201 Created.
 */
@Getter
@Builder
@Schema(description = "Ответ после успешной регистрации пользователя")
public class UserRegisteredResponse {

    @Schema(description = "ID созданного пользователя", example = "123")
    private Long userId;

    @Schema(description = "Email пользователя", example = "user@example.com")
    private String email;

    @Schema(description = "Полное имя пользователя", example = "Иван Иванов")
    private String fullName;

    @Schema(description = "Дата и время регистрации", example = "2024-01-15T10:30:00")
    private LocalDateTime registeredAt;

    @Schema(description = "Сообщение об успешной регистрации", example = "Пользователь успешно зарегистрирован")
    private String message;
}