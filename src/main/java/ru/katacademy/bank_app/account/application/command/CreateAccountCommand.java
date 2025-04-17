package ru.katacademy.bank_app.account.application.command;

import lombok.Getter;
import ru.katacademy.bank_app.account.domain.enumtype.AccountStatus;
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
 * Дата: 2025-04-17
 */

@Getter
public class CreateAccountCommand {
    private final BigDecimal initialAmount;
    private final Currency currency;
    private final AccountStatus status = AccountStatus.ACTIVE;
    private final AccountNumber accountNumber;

    public CreateAccountCommand(BigDecimal initialAmount, Currency currency,
                                AccountNumber accountNumber) {
        this.initialAmount = initialAmount;
        this.currency = currency;
        this.accountNumber = accountNumber;
    }
}

