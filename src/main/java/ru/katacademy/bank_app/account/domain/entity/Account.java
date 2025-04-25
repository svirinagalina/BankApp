package ru.katacademy.bank_app.account.domain.entity;

import lombok.Getter;
import ru.katacademy.bank_app.account.domain.enumtype.AccountStatus;
import ru.katacademy.bank_app.account.domain.enumtype.Currency;
import ru.katacademy.bank_app.shared.exception.BusinessRuleViolationException;
import ru.katacademy.bank_app.shared.exception.CurrencyMismatchException;
import ru.katacademy.bank_app.shared.valueobject.AccountNumber;
import ru.katacademy.bank_app.shared.valueobject.Money;

import java.math.BigDecimal;

/**
 * Класс, представляющий банковский счет.
 * <p>
 * Содержит информацию о текущем статусе счета и предоставляет методы
 * для управления этим статусом (блокировка, закрытие, проверка активности).
 * </p>
 * <p>
 * Поля:
 * - money: баланс счета
 * - status: текущий статус счета. Определяет доступность счета для операций.
 * - currency: валюта счета.
 * - accountNumber: номер счета
 *
 * @author Sheffy
 */
@Getter
public class Account {
    private Money money;
    private AccountStatus status;
    private final AccountNumber accountNumber;


    public Account(AccountNumber accountNumber, Money money, AccountStatus status) {
        if (money == null || money.amount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial balance cannot " +
                    "be null or negative.");
        }
        if (accountNumber == null) {
            throw new IllegalArgumentException("Account number cannot be null.");
        }
        this.money = money;
        this.accountNumber = accountNumber;
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
     * Пополняет баланс
     *
     * @param money объект предоставляющий сумму валюты и вид валюты
     */
    public void deposit(Money money) {
        if (!isActive()) {
            throw new IllegalStateException("Cannot deposit to inactive account.");
        }
        if (money.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be " +
                    "greater than zero.");
        }
        this.money.checkCurrencyMatch(money);
        this.money = this.money.add(money);
    }

    /**
     * Метод реализует уменьшение баланса счета на заданную сумму.
     *
     * @param amount сумма денег, которую клиент хочет снять со счета.
     */
    public void withdraw(Money amount) {
        if (!isActive()) {
            throw new IllegalStateException("Account is not active. " +
                    "Withdrawal not allowed.");
        }
        this.money = this.money.subtract(amount);
    }

    /**
     * Проверяет, совпадают ли валюты текущего аккаунта и переданного объекта {@link Money}.
     *
     * @param money объект {@link Money} для сравнения
     * @throws CurrencyMismatchException если валюты не совпадают
     */
    private void checkCurrencyMismatch(Money money) {
        if (this.money.currency().equals(money.currency())) {
            throw new CurrencyMismatchException(
                    String.format("Нельзя выполнить операцию: валюты не совпадают (%s ≠ %s)",
                            this.money.currency(), money.currency())
            );
        }
    }

    /**
     * Проверяет, можно ли вычесть указанную сумму без получения отрицательного результата.
     *
     * @param other объект {@link Money} для сравнения
     * @throws BusinessRuleViolationException если результат будет отрицательным
     */
    private void checkSubtractionAllowed(Money other) {
        if (this.money.amount().compareTo(other.amount()) < 0) {
            throw new BusinessRuleViolationException("Недостаточно средств");
        }
    }

    /**
     * Проверяет возможность снятия средств с текущего аккаунта.
     * Метод проверяет:
     * <ul>
     *     <li>Активен ли аккаунт</li>
     *     <li>Совпадает ли валюта</li>
     *     <li>Достаточность средств для снятия</li>
     * </ul>
     *
     * @param money сумма для снятия
     * @throws BusinessRuleViolationException если одна из проверок не пройдена
     */
    public void canWithdraw(Money money) {
        if (!isActive()) {
            throw new BusinessRuleViolationException("Невозможно снять средства: аккаунт неактивен.");
        }
        checkCurrencyMismatch(money);
        checkSubtractionAllowed(money);
    }

    /**
     * Проверяет возможность перевода средств на указанный аккаунт.
     * Метод проверяет:
     * <ul>
     *     <li>Активность аккаунта получателя</li>
     *     <li>Совпадение валют</li>
     * </ul>
     *
     * @param account аккаунт получателя
     * @throws BusinessRuleViolationException если целевой аккаунт не активен
     * @throws CurrencyMismatchException      если валюты не совпадают
     */
    public void canTransferTo(Account account) {
        if (!account.isActive()) {
            throw new BusinessRuleViolationException("Невозможно снять средства: аккаунт неактивен");
        }
        checkCurrencyMismatch(account.money);
    }

    /**
     * Проверяет возможность перевода указанной суммы с текущего аккаунта на другой.
     * Метод выполняет проверки:
     * <ul>
     *     <li>Активность целевого аккаунта</li>
     *     <li>Достаточность средств, совпадение валют и активность текущего аккаунта</li>
     * </ul>
     * В случае ошибок выбрасывается {@link BusinessRuleViolationException} или {@link CurrencyMismatchException}.
     *
     * @param target аккаунт получателя
     * @param amount сумма для перевода
     * @throws BusinessRuleViolationException если:
     *         <ul>
     *             <li>Целевой аккаунт неактивен</li>
     *             <li>Недостаточно средств на текущем аккаунте</li>
     *             <li>Не совпадают валюты</li>
     *         </ul>
     */
    public void validateTransferTo(Account target, Money amount) {
        canTransferTo(target);
        canWithdraw(amount);
    }


    /**
     * Проверяет равенство аккаунтов по {@link AccountNumber}.
     *
     * @param o объект для сравнения
     * @return {@code true}, если оба объекта являются Account и
     * имеют одинаковый номер счёта
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ru.katacademy.bank_app.account.domain.entity.Account account =
                (ru.katacademy.bank_app.account.domain.entity.Account) o;
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
