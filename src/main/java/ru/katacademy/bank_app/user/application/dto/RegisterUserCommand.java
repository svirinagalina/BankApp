package ru.katacademy.bank_app.user.application.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public record RegisterUserCommand(

        @NotBlank(message = "Имя не может быть пустым")
        String fullName,

        @Email(message = "Некорректный email адрес")
        String email,

        @NotBlank(message = "Пароль не может быть пустым")
        String password
) {
}
