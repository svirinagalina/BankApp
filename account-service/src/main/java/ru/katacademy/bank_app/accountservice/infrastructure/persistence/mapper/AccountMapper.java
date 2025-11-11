package ru.katacademy.bank_app.accountservice.infrastructure.persistence.mapper;

import ru.katacademy.bank_app.accountservice.domain.entity.Account;
import ru.katacademy.bank_app.accountservice.infrastructure.persistence.entity.AccountEntity;

/**
 * Маппер между доменной моделью {@link Account} и JPA‑сущностью {@link AccountEntity}.
 */
public class AccountMapper {

    /**
     * Доменную модель в JPA‑сущность.
     *
     * @param account доменный аккаунт
     * @return JPA‑сущность для сохранения
     */
    public static AccountEntity toEntity(Account account) {
        return new AccountEntity(
                account.getAccountNumber(),
                account.getUser(),
                account.getBalance(),
                account.getStatus(),
                account.getCreatedAt()
        );
    }

    /**
     * JPA‑сущность в доменную модель.
     *
     * @param accountEntity сущность из БД
     * @return доменный аккаунт
     */
    public static Account toDomain(AccountEntity accountEntity) {
        return new Account(
                accountEntity.getId(),
                accountEntity.getUser(),
                accountEntity.getAccountNumber(),
                accountEntity.getBalance(),
                accountEntity.getStatus(),
                accountEntity.getCreatedAt()
        );
    }
}
