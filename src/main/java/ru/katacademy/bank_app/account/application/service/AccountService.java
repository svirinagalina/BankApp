package ru.katacademy.bank_app.account.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.katacademy.bank_app.account.domain.entity.Account;
import ru.katacademy.bank_app.shared.exception.AccountInactiveException;
import ru.katacademy.bank_app.shared.exception.InsufficientFundsException;
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
     * @throws AccountInactiveException   если аккаунт одной из сторон не активен
     * @throws InsufficientFundsException если на счёте отправителя недостаточно денег
     */
    @Transactional
    public void transfer(Account accountFrom, Account accountTo, Money amount) {
        if (!accountFrom.isActive()) {
            throw new AccountInactiveException("Аккаунт отправителя неактивен");
        }
        if (!accountTo.isActive()) {
            throw new AccountInactiveException("Аккаунт получателя неактивен");
        }
        accountFrom.withdraw(amount);
        accountTo.deposit(amount);
    }
}
