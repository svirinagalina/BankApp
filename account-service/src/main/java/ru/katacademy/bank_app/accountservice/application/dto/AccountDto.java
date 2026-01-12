package ru.katacademy.bank_app.accountservice.application.dto;

import java.time.LocalDateTime;
import ru.katacademy.bank_app.accountservice.domain.enumtype.AccountStatus;
import ru.katacademy.bank_shared.valueobject.AccountNumber;
import ru.katacademy.bank_shared.valueobject.Money;

/**
 * DTO для передачи данных о банковском аккаунте между слоями приложения.
 * Используется для возврата информации в presentation и application слоях.
 * <p>
 * Поля:
 * - id: идентификатор аккаунта
 * - accountNumber: номер аккаунта
 * - userId: id пользователя, которому принадлежит аккаунт
 * - balance: баланс аккаунта
 * - status: статус аккаунта
 * <p>
 */
public record AccountDto(
        Long id,
        AccountNumber accountNumber,
        Long userId,
        Money balance,
        AccountStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}