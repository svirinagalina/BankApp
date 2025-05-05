package ru.katacademy.bank_app.notification.template;

import ru.katacademy.bank_app.account.domain.entity.Account;
import ru.katacademy.bank_shared.valueobject.Money;

/**
 * Шаблон уведомления о переводе средств между счетами.
 * <p>
 * Содержит статический метод для форматирования текста уведомления,
 * используя данные отправителя, получателя и суммы перевода.
 * </p>
 *
 * @author Sheffy
 */
public class TransferNotificationTemplate {

    /**
     * Форматирует уведомление о переводе между счетами.
     *
     * @param from   аккаунт отправителя
     * @param to     аккаунт получателя
     * @param amount сумма перевода
     * @return строка с текстом уведомления
     */
    public static String format(Account from, Account to, Money amount) {
        return String.format("Перевод со счёта %s на счёт %s в количестве %s",
                from.getAccountNumber().value(),
                to.getAccountNumber().value(),
                amount.toString()
        );
    }
}

