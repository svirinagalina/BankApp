package ru.katacademy.bank_app.account.infrastructure.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.katacademy.bank_app.account.domain.enumtype.AccountStatus;
import ru.katacademy.bank_app.shared.valueobject.AccountNumber;
import ru.katacademy.bank_app.shared.valueobject.Money;

/**
 * Сущность банковского счета для работы с БД.
 * Хранит основные данные счета: номер, баланс и статус.
 *
 * @author Sheffy
 */
@Getter
@AllArgsConstructor
public class AccountEntity {
    /** Уникальный номер счета */
    private final AccountNumber accountNumber;

    /** Текущий баланс счета */
    @Setter
    private Money money;

    /** Текущий статус счета */
    @Setter
    private AccountStatus status;
}


