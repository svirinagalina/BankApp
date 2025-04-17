package ru.katacademy.bank_app.account.application.mapper;

import ru.katacademy.bank_app.account.domain.entity.Account;
import ru.katacademy.bank_app.account.dto.AccountDto;

/**
 * Используется для преобразования данных между различными уровнями приложения.
 * В данном случае, он служит для преобразования сущность в DTO и обратно.
 * <p>
 * Автор: Maxim4212
 * Дата: 2025-04-17
 */

public class AccountMapper {
    /**
     * Метод нужен для преобразования аккаунта (сущности) в DTO
     *
     * @param account аккаунт, который мы преобразуем в DTO
     * @return что возвращает новый DTO с валютой и статусом аккаунта
     */
    public static AccountDto toDto(Account account) {
        return new AccountDto(
                account.getMoney().amount(),
                account.getCurrency().name(),
                account.getStatus().name(),
                account.getAccountNumber().toString()
        );
    }
}

