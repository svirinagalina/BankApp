package ru.katacademy.bank_app.frauddetection.client;

import ru.katacademy.bank_shared.valueobject.AccountStatus;
import ru.katacademy.bank_shared.valueobject.Money;

public record AccountDto(
        Long id,
        String accountNumber,
        Long userId,
        Money balance,
        AccountStatus status
) {
}
