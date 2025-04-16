package ru.katacademy.bank_app.account.domain;

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
    private Money money;

    /**
     * Создаёт аккаунт с указанным денежным балансом.
     *
     * @param money начальный баланс аккаунта
     */
    public Account(Money money) {
        this.money = money;
    }

    /**
     * Пополняет баланс
     *
     * @param money объект предоставляющий сумму валюты и вид валюты
     * @return Money c пополненным балансом
     */
    public Money deposit(Money money) {
        this.money = this.money.add(money);
        return this.money;
    }

    /**
     * Вычитает баланс
     *
     * @param money объект предоставляющий сумму валюты и вид валюты
     * @return Money с вычтенным балансом
     * @throws InsufficientFundsException если баланс меньше вычитаемой суммы
     */
    public Money withdraw(Money money) {
        if (this.money.amount().compareTo(money.amount()) >= 0) {
            this.money = this.money.subtract(money);
            return this.money;
        } else {
            throw new InsufficientFundsException("Недостаточно средств для снятия.");
        }
    }
}
