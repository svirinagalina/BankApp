package ru.katacademy.bank_app.account.domain.entity;

import lombok.Getter;
import ru.katacademy.bank_app.account.domain.enumtype.AccountStatus;
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
@Getter
public class Account {

    private final AccountNumber accountNumber;

    private Money money;
    /**
     * Текущий статус счета.
     * Определяет доступность счета для операций.
     */
    private AccountStatus status;

    public Account(AccountNumber accountNumber, Money money, AccountStatus status) {
        this.accountNumber = accountNumber;
        this.money = money;
        this.status = status;
    }

    /**
     * Проверяет, является ли счет активным.
     *
     * @return {@code true} если счет имеет статус {@link AccountStatus#ACTIVE},
     * {@code false} в противном случае
     */
    public boolean isActive() {
        return status == AccountStatus.ACTIVE;
    }

    /**
     * Блокирует счет.
     * <p>
     * Устанавливает статус счета в {@link AccountStatus#BLOCKED},
     * что делает его недоступным для операций.
     * </p>
     */
    public void blockAccount() {
        status = AccountStatus.BLOCKED;
    }

    /**
     * Закрывает счет.
     * <p>
     * Устанавливает статус счета в {@link AccountStatus#CLOSE},
     * после чего операции по счету становятся невозможны.
     * </p>
     */
    public void closeAccount() {
        status = AccountStatus.CLOSE;
    }

    /**
     * Создаёт аккаунт с указанным денежным балансом и номером счета.
     *
     * @param accountNumber номер счета аккаунта
     * @param money         начальный баланс аккаунта
     */

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
