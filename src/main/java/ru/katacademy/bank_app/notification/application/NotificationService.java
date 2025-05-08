package ru.katacademy.bank_app.notification.application;

import ru.katacademy.bank_app.account.domain.entity.Account;
import ru.katacademy.bank_shared.valueobject.Money;

/**
 * Сервис для отправки уведомлений о банковских операциях.
 * @author Sheffy
 */
public interface NotificationService {
    /**
     * Отправляет уведомление о переводе средств.
     *
     * @param fromAccount аккаунт отправителя
     * @param toAccount аккаунт получателя
     * @param amount сумма перевода
     *
     */
    void sendTransferNotification(Account fromAccount, Account toAccount, Money amount);
}