package ru.katacademy.bank_app.accountservice.infrastructure.persistence.mapper;


import ru.katacademy.bank_app.accountservice.domain.entity.Account;
import ru.katacademy.bank_app.accountservice.infrastructure.persistence.entity.AccountEntity;

/**
 * Маппер между доменной моделью {@link Account} и JPA‑сущностью {@link AccountEntity}.
 */
public class AccountMapper {

    /**
     * Доменную модель → JPA‑сущность.
     *
     * @param u доменный аккаунт
     * @return JPA‑сущность для сохранения
     */
    public static AccountEntity toEntity(Account u) {
        return new AccountEntity(
                u.getAccountNumber(),
                u.getUser(),
                u.getBalance(),
                u.getStatus(),
                u.getCreatedAt()
        );
    }

    /**
     * JPA‑сущность → доменную модель.
     *
     * @param e сущность из БД
     * @return доменный аккаунт
     */
    public static Account toDomain(AccountEntity e) {
        return new Account(
                e.getId(),
                e.getUser(),
                e.getAccountNumber(),
                e.getBalance(),
                e.getStatus(),
                e.getCreatedAt()
        );
    }
}
