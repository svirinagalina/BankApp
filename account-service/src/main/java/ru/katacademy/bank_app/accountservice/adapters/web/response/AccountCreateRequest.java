package ru.katacademy.bank_app.accountservice.adapters.web.response;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * DTO для создания нового банковского счета.
 * Использует простые типы для валидации.
 */
@Data
public class AccountCreateRequest {

    @NotNull(message = "ID пользователя не может быть пустым")
    @Positive(message = "ID пользователя должен быть положительным числом")
    private Long userId;

    @NotBlank(message = "Номер счета не может быть пустым")
    @Pattern(regexp = "^\\d{20}$", message = "Номер счета должен содержать ровно 20 цифр")
    private String accountNumber;

    @NotNull(message = "Начальный баланс не может быть пустым")
    @DecimalMin(value = "0.0", inclusive = true, message = "Начальный баланс не может быть отрицательным")
    @DecimalMax(value = "1000000.0", message = "Начальный баланс слишком велик")
    private BigDecimal initialBalance;

    @NotBlank(message = "Валюта счета не может быть пустой")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Валюта должна быть в формате ISO (3 заглавные буквы)")
    private String currency;
}