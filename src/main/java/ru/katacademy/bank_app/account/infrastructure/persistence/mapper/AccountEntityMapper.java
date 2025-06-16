package ru.katacademy.bank_app.account.infrastructure.persistence.mapper;

import ru.katacademy.bank_app.account.domain.entity.Account;
import ru.katacademy.bank_app.account.infrastructure.persistence.entity.AccountEntity;

/**
 * Маппер для конвертации между доменной моделью и persistence сущностью счета.
 *
 * @author Sheffy
 */
public class AccountEntityMapper {

    /**
     * Приватный конструктор предотвращает создание экземпляра утилитарного класса.
     */
    private AccountEntityMapper() {
    }

    /**
     * Конвертирует доменную модель в persistence сущность.
     *
     * @param account доменная модель счета
     * @return persistence сущность
     */
    public static AccountEntity toAccountEntity(Account account) {
        return new AccountEntity(
                account.getAccountNumber(),
                account.getMoney(),
                account.getStatus()
        );
    }

    /**
     * Конвертирует persistence сущность в доменную модель.
     *
     * @param accountEntity persistence сущность счета
     * @return доменная модель
     */
    public static Account toAccount(AccountEntity accountEntity) {
        return Account.newAccount(
                accountEntity.getAccountNumber(),
                accountEntity.getMoney(),
                accountEntity.getStatus()
        );
    }
}