package ru.katacademy.bank_app.accountservice.adapters.web.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Запрос на изменение пароля пользователя.
 */
@Getter
@Setter
@Schema(description = "Запрос на изменение пароля")
public class ChangePasswordRequest {

    @NotNull(message = "ID пользователя не может быть null")
    @Schema(description = "ID пользователя", example = "123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userId;

    @NotBlank(message = "Текущий пароль не может быть пустым")
    @Schema(description = "Текущий пароль пользователя", example = "oldPassword123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String currentPassword;

    @NotBlank(message = "Новый пароль не может быть пустым")
    @Size(min = 6, max = 100, message = "Длина нового пароля должна быть от 6 до 100 символов")
    @Schema(description = "Новый пароль", example = "newSecurePassword456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String newPassword;

    @NotBlank(message = "Подтверждение пароля не может быть пустым")
    @Schema(description = "Подтверждение нового пароля", example = "newSecurePassword456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String confirmPassword;
}