package ru.katacademy.bank_app.accountservice.domain.mapper;

import org.springframework.stereotype.Component;
import ru.katacademy.bank_app.accountservice.application.dto.AccountDto;
import ru.katacademy.bank_app.accountservice.domain.entity.Account;

/**
 * Маппер для преобразования {@link Account} в {@link AccountDto}.
 * <p>
 * Автор: Набеев В.В.
 * Дата: 2025-10-14
 */
@Component
public class AccountMapper {
    /**
     *
     * Преобразует сущность Account в DTO.
     *
     * @param account аккаунт
     * @return DTO аккаунта
     */
    public AccountDto toDto(Account account) {
        return new AccountDto(
                account.getId(),
                account.getAccountNumber().value(),
                account.getUser().getId(),
                account.getBalance(),
                account.getStatus()
        );
    }
}
