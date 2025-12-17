package ru.katacademy.bank_app.accountservice.adapters.web.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Запрос на регистрацию нового пользователя.
 * Используется record, чтобы быть совместимым с доменным слоем.
 */
@Schema(description = "Запрос на регистрацию пользователя")
public record RegisterUserRequest(

        @NotBlank(message = "Полное имя не может быть пустым")
        @Size(min = 2, max = 100, message = "Длина имени должна быть от 2 до 100 символов")
        @Schema(description = "Полное имя пользователя", example = "Иван Иванов", requiredMode = Schema.RequiredMode.REQUIRED)
        String fullName,

        @NotBlank(message = "Email не может быть пустым")
        @Email(message = "Некорректный формат email")
        @Schema(description = "Email адрес пользователя", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
        String email,

        @NotBlank(message = "Пароль не может быть пустым")
        @Size(min = 6, max = 100, message = "Длина пароля должна быть от 6 до 100 символов")
        @Schema(description = "Пароль пользователя", example = "securePassword123", requiredMode = Schema.RequiredMode.REQUIRED)
        String password

) {}