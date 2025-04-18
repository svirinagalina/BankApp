package ru.katacademy.bank_app.account.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.katacademy.bank_app.account.domain.entity.Account;
import ru.katacademy.bank_app.shared.valueobject.Money;

/**
 * Сервис для выполнения операций со счетами.
 */
@Service
public class AccountService {
    /**
     * Переводит денежные средства от одного аккаунта к другому.
     * Сначала выполняется списание средств с аккаунта отправителя,
     * затем зачисление на аккаунт получателя.
     * <p>
     * Метод помечен как {@code @Transactional}, чтобы, обеспечить атомарность операции:
     * если одна из операций завершится с ошибкой, изменения будут откатаны.
     * </p>
     *
     * @param accountFrom аккаунт отправителя
     * @param accountTo   аккаунт получателя
     * @param amount      сумма перевода (объект {@link Money})
     * @throws IllegalStateException если возникает ошибка при списании или зачислении средств
     */
    @Transactional
    public void transfer(Account accountFrom, Account accountTo, Money amount) {
        try {
            accountFrom.withdraw(amount);
        } catch (Exception e) {
            throw new IllegalStateException("Ошибка при списании средств со счёта отправителя", e);
        }

        try {
            accountTo.deposit(amount);
        } catch (Exception e) {
            throw new IllegalStateException("Ошибка при зачислении средств на счёт получателя", e);
        }
    }
}
