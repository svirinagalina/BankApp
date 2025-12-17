package ru.katacademy.bank_app.accountservice.adapters.web.response.account;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "Ответ с данными банковского счета")
public class AccountResponse {

    @Schema(description = "ID счета", example = "456")
    private Long id;

    @Schema(description = "Номер счета", example = "40817810099910004321")
    private String accountNumber;

    @Schema(description = "ID владельца счета", example = "123")
    private Long userId;

    @Schema(description = "Баланс счета", example = "15000.75")
    private BigDecimal balance;

    @Schema(description = "Валюта счета", example = "RUB")
    private String currency;

    @Schema(description = "Статус счета",
            example = "ACTIVE",
            allowableValues = {"ACTIVE", "BLOCKED", "CLOSED", "PENDING", "FROZEN"})
    private String status;

    @Schema(description = "Дата и время создания счета", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Дата и время последнего обновления", example = "2024-01-20T14:45:00")
    private LocalDateTime updatedAt;

}