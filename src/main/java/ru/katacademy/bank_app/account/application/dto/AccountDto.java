package ru.katacademy.bank_app.account.application.dto;

import ru.katacademy.bank_app.account.domain.enumtype.AccountStatus;
import ru.katacademy.bank_app.shared.valueobject.AccountNumber;
import ru.katacademy.bank_app.shared.valueobject.Currency;

import java.math.BigDecimal;

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
         * Валюта счета
         * @see Currency
         */
        Currency currency,

        /**
         * Текущий баланс счета
         * <p>
         * Значение представлено в минимальных единицах валюты (например, копейки для RUB)
         * </p>
         */
        BigDecimal balance,

        /**
         * Текущий статус счета
         * @see AccountStatus
         */
        AccountStatus status
) {
}