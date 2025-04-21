package ru.katacademy.bank_app.account.application.command;

import ru.katacademy.bank_app.account.domain.enumtype.Currency;
import ru.katacademy.bank_app.shared.valueobject.AccountNumber;

import java.math.BigDecimal;

/**
 * Это класс-команда, который используется для передачи данных при создании аккаунта.
 * <p>
 * Поля:
 * - initialAmount: начальная сумма,с которой создаётся счёт
 * - currency: валюту нового счёта
 * - status: статус нового счёта
 * - accountNumber: номер счета
 * <p>
 * Автор: Maxim4212
 * Дата: 2025-04-21
 */

public record CreateAccountCommand(
        Long userId,
        Currency currency,
        BigDecimal initialAmount,
        AccountNumber accountNumber
) {
}



