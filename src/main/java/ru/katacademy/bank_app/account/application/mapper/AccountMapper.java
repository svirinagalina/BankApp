package ru.katacademy.bank_app.account.application.mapper;

import ru.katacademy.bank_app.account.application.dto.AccountDto;
import ru.katacademy.bank_app.account.domain.entity.Account;

/**
 * Маппер для преобразования доменной модели {@link Account} в DTO {@link AccountDto}.
 * <p>
 * Содержит статические методы конвертации между слоями приложения.
 * Не содержит состояния и бизнес-логики.
 * </p>
 *
 * @author Sheffy
 */
public class AccountMapper {

    /**
     * Преобразует доменную модель счета в транспортный объект.
     *
     * @param account доменная модель счета (не должна быть null)
     * @return новый экземпляр {@link AccountDto}
     */
    public static AccountDto toAccountDto(Account account) {

        return new AccountDto(
                account.getAccountNumber(),
                account.getMoney(),
                account.getStatus()
        );

    }
}