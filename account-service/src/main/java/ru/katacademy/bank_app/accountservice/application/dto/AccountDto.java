package ru.katacademy.bank_app.accountservice.application.dto;

import ru.katacademy.bank_app.accountservice.domain.enumtype.AccountStatus;
import ru.katacademy.bank_shared.valueobject.Money;

public record AccountDto(
        Long id,
        String accountNumber,
        Long userId,
        Money balance,
        AccountStatus status
) {
}
