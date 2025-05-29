package ru.katacademy.bank_app.accountservice.application.command;

import lombok.*;

/**
 * DTO-комманда для смены пароля
 *
 * Автор: Колпаков А.С..
 * Дата: 2025-04-30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordCommand {
    private Long userId;
    private String oldPassword;
    private String newPassword;
}

