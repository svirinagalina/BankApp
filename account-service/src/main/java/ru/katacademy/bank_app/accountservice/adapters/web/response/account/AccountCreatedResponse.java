package ru.katacademy.bank_app.accountservice.adapters.web.response.account;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Ответ после успешного создания банковского счета.
 * Возвращается со статусом 201 Created.
 */
@Getter
@Builder
@Schema(description = "Ответ после успешного создания счета")
public class AccountCreatedResponse {

    @Schema(description = "ID созданного счета", example = "456")
    private Long accountId;

    @Schema(description = "Номер счета", example = "40817810099910004321")
    private String accountNumber;

    @Schema(description = "ID владельца счета", example = "123")
    private Long userId;

    @Schema(description = "Начальный баланс", example = "1000.00")
    private BigDecimal initialBalance;

    @Schema(description = "Валюта счета", example = "RUB")
    private String currency;

    @Schema(description = "Дата и время создания", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Сообщение об успешном создании", example = "Счет успешно создан")
    private String message;
}