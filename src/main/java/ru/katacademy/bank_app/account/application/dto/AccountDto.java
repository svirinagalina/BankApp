package ru.katacademy.bank_app.account.application.dto;

import ru.katacademy.bank_app.account.domain.enumtype.AccountStatus;
import ru.katacademy.bank_shared.valueobject.AccountNumber;
import ru.katacademy.bank_shared.valueobject.Money;

/**
 * DTO (Data Transfer Object) для передачи данных о банковском счете.
 * <p>
 * Используется для обмена информацией о счете между слоями приложения
 * без раскрытия внутренней доменной логики.
 * </p>
 *
 * @author Sheffy
 */
public record AccountDto(
        /**
         * Уникальный номер счета
         * @see AccountNumber
         */
        AccountNumber accountNumber,

        /**
         * Баланс + валюта счета
         * @see Money
         */
        Money amount,

        /**
         * Текущий статус счета
         * @see AccountStatus
         */
        AccountStatus status
) {
}
