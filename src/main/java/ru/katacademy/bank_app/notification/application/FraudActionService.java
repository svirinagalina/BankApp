package ru.katacademy.bank_app.notification.application;

import ru.katacademy.bank_shared.valueobject.AccountNumber;

/**
 * Сервис для выполнения действий, связанных с обнаружением мошенничества.
 * <p>
 * Автор: Maxim4212
 * Дата: 2025-04-30
 */
public interface FraudActionService {
    /**
     * Блокирует аккаунт по его номеру в случае подозрения на мошенничество.
     *
     * @param accountNumber номер аккаунта, подлежащего блокировке
     */
    void blockAccount(AccountNumber accountNumber);
}
