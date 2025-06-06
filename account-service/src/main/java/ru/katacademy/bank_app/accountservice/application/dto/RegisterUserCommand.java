package ru.katacademy.bank_app.accountservice.application.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO-класс для регистрации нового пользователя.
 * <p>
 * Содержит данные, необходимые для создания учетной записи пользователя.
 * Все поля валидируются перед использованием.
 * </p>
 *
 * @author Sheffy
 */
public record RegisterUserCommand(
        /**
         * Полное имя пользователя.
         * <p>
         * Не может быть пустым. Валидируется аннотацией {@link NotBlank}.
         * </p>
         */
        @NotBlank(message = "Имя не может быть пустым")
        String fullName,

        /**
         * Email адрес пользователя.
         * <p>
         * Должен соответствовать формату email. Валидируется аннотацией {@link Email}.
         * </p>
         */
        @Email(message = "Некорректный email адрес")
        String email,

        /**
         * Пароль пользователя.
         * <p>
         * Не может быть пустым. Валидируется аннотацией {@link NotBlank}.
         * </p>
         */
        @NotBlank(message = "Пароль не может быть пустым")
        String password
) {
}