package ru.katacademy.bank_app.accountservice.adapters.web.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import ru.katacademy.bank_app.accountservice.adapters.web.response.account.AccountCreatedResponse;
import ru.katacademy.bank_app.accountservice.adapters.web.response.account.AccountResponse;
import ru.katacademy.bank_app.accountservice.application.dto.AccountDto;
import ru.katacademy.bank_shared.valueobject.AccountNumber;
import ru.katacademy.bank_shared.valueobject.Currency;
import ru.katacademy.bank_shared.valueobject.Money;

public class AccountWebMapper {

    private AccountWebMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static AccountResponse toAccountResponse(AccountDto dto) {
        if (dto == null) {
            return null;
        }

        return AccountResponse.builder()
                .id(dto.id())
                .accountNumber(extractAccountNumber(dto))
                .userId(dto.userId())
                .balance(extractBalanceAmount(dto))
                .currency(extractCurrencyCode(dto))
                .status(extractStatusName(dto))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static AccountCreatedResponse toAccountCreatedResponse(AccountDto dto) {
        if (dto == null) {
            return null;
        }

        return AccountCreatedResponse.builder()
                .accountId(dto.id())
                .accountNumber(extractAccountNumber(dto))
                .userId(dto.userId())
                .initialBalance(extractBalanceAmount(dto))
                .currency(extractCurrencyCode(dto))
                .createdAt(LocalDateTime.now())
                .message("Счет успешно создан")
                .build();
    }

    private static String extractAccountNumber(AccountDto dto) {
        final AccountNumber accountNumber = dto.accountNumber();
        if (accountNumber == null) {
            return null;
        }
        return accountNumber.value();
    }

    private static BigDecimal extractBalanceAmount(AccountDto dto) {
        final Money balance = dto.balance();
        if (balance == null) {
            return null;
        }
        return balance.amount();
    }

    private static String extractCurrencyCode(AccountDto dto) {
        final Money balance = dto.balance();
        if (balance == null) {
            return null;
        }

        final Currency currency = balance.currency();
        if (currency == null) {
            return null;
        }
        return currency.code();
    }

    private static String extractStatusName(AccountDto dto) {
        if (dto.status() == null) {
            return null;
        }
        return dto.status().name();
    }
}