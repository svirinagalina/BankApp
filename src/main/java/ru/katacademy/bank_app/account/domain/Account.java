package ru.katacademy.bank_app.account.domain;

import ru.katacademy.bank_app.shared.valueobject.AccountNumber;
import ru.katacademy.bank_app.shared.valueobject.Money;
import ru.katacademy.bank_app.shared.exception.InsufficientFundsException;

/**
 * Представляет аккаунт с денежным балансом, включающим сумму и валюту.
 *
 * <p>
 * Класс гарантирует что:
 * <ul>
 *     <li>Сумма вычета и сложения не может привести к отрицательному значению</li>
 * </ul>
 */
public class Account {
    private final AccountNumber accountNumber;
    private Money money;

    /**
     * Создаёт аккаунт с указанным денежным балансом и номером счета.
     *
     * @param accountNumber номер счета аккаунта
     * @param money         начальный баланс аккаунта
     */
    public Account(AccountNumber accountNumber, Money money) {
        this.accountNumber = accountNumber;
        this.money = money;
    }

    /**
     * Пополняет баланс
     *
     * @param money объект предоставляющий сумму валюты и вид валюты
     */
    public void deposit(Money money) {
        this.money = this.money.add(money);
    }

    /**
     * Вычитает баланс
     *
     * @param money объект предоставляющий сумму валюты и вид валюты
     * @throws InsufficientFundsException если баланс меньше вычитаемой суммы
     */
    public void withdraw(Money money) {
        if (this.money.amount().compareTo(money.amount()) < 0) {
            this.money = this.money.subtract(money);
        } else {
            throw new InsufficientFundsException("Недостаточно средств для снятия.");
        }
    }

    /**
     * Проверяет равенство аккаунтов по {@link AccountNumber}.
     *
     * @param o объект для сравнения
     * @return {@code true}, если оба объекта являются Account и имеют одинаковый номер счёта
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return accountNumber.equals(account.accountNumber);
    }

    /**
     * Возвращает хэш-код на основе {@link AccountNumber}.
     *
     * @return хэш-код аккаунта
     */
    @Override
    public int hashCode() {
        return accountNumber.hashCode();
    }
}
