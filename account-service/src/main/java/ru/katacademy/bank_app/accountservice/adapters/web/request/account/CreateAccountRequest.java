package ru.katacademy.bank_app.accountservice.adapters.web.request.account;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Запрос на создание банковского счета")
public class CreateAccountRequest {

    @NotBlank(message = "ID пользователя не может быть пустым")
    @Pattern(regexp = "^\\d+$", message = "ID пользователя должен быть числом")
    @Schema(description = "ID пользователя-владельца счета",
            example = "123",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String userId;

    @NotBlank(message = "Номер счета не может быть пустым")
    @Size(min = 20, max = 20, message = "Номер счета должен содержать 20 символов")
    @Pattern(regexp = "^\\d+$", message = "Номер счета должен содержать только цифры")
    @Schema(description = "Номер банковского счета (20 цифр)",
            example = "40817810099910004321",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String accountNumber;

    @NotNull(message = "Начальный баланс не может быть null")
    @PositiveOrZero(message = "Начальный баланс не может быть отрицательным")
    @Digits(integer = 15, fraction = 2, message = "Баланс не может иметь больше 15 целых и 2 дробных цифр")
    @Schema(description = "Начальный баланс счета",
            example = "1000.00",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal initialBalance;

    @NotBlank(message = "Валюта не может быть пустой")
    @Size(min = 3, max = 3, message = "Код валюты должен содержать 3 символа")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Неверный формат валюты")
    @Schema(description = "Код валюты счета (ISO 4217)",
            example = "RUB",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String currency;

    @Pattern(regexp = "CHECKING|SAVINGS|DEPOSIT|CREDIT",
            message = "Недопустимый тип счета")
    @Schema(description = "Тип счета",
            example = "CHECKING",
            allowableValues = {"CHECKING", "SAVINGS", "DEPOSIT", "CREDIT"},
    requiredMode = Schema.RequiredMode.REQUIRED)
    private String accountType;
}