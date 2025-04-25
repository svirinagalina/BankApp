package ru.katacademy.bank_app.account.domain.entity;

import lombok.Getter;
import ru.katacademy.bank_app.account.domain.enumtype.AccountStatus;
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
        if (status == null) {
            throw new IllegalArgumentException("Account status cannot be null.");
        }
        this.money = money;
        this.accountNumber = accountNumber;
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
     * Пополняет баланс
     *
     * @param money объект предоставляющий сумму валюты и вид валюты
     * @throws BusinessRuleViolationException если аккаунт не активен или депозит равен нулю
     * @throws CurrencyMismatchException      если валюты не совпадают
     */
    public void deposit(Money money) {
        validateDepositAllowed(money);
        this.money = this.money.add(money);
    }

    /**
     * Метод реализует уменьшение баланса счета на заданную сумму.
     *
     * @param amount сумма денег, которую клиент хочет снять со счета.
     * @throws BusinessRuleViolationException если аккаунт не активен или недостаточно средств для снятия
     * @throws CurrencyMismatchException      если валюты не совпадают
     */
    public void withdraw(Money amount) {
        validateWithdrawalAllowed(amount);
        this.money = this.money.subtract(amount);
    }

    /**
     * Проверяет возможность снятия средств с текущего аккаунта.
     * Метод использует {@link #validateWithdrawalAllowed(Money)} для проверки:
     * <ul>
     *     <li>Активен ли аккаунт</li>
     *     <li>Совпадает ли валюта</li>
     *     <li>Достаточность средств для снятия</li>
     * </ul>
     *
     * @param money сумма для снятия
     * @return true если аккаунт активен, валюты совпадают и достаточно средств
     * @throws BusinessRuleViolationException если одна из проверок не пройдена
     * @throws CurrencyMismatchException      если валюты не совпадают
     */
    public boolean canWithdraw(Money money) {
        validateWithdrawalAllowed(money);
        return true;
    }

    /**
     * Метод проверяет:
     * <ul>
     *     <li>Активен ли аккаунт</li>
     *     <li>Совпадает ли валюта</li>
     *     <li>Достаточность средств для снятия</li>
     * </ul>
     *
     * @param other объект {@link Money} для сравнения
     * @throws BusinessRuleViolationException если результат будет отрицательным
     * @throws CurrencyMismatchException      если валюты не совпадают
     */
    private void validateWithdrawalAllowed(Money other) {
        if (!isActive()) {
            throw new BusinessRuleViolationException("Невозможно снять средства: аккаунт получателя неактивен");
        }
        checkCurrencyEquality(other);
        if (this.money.amount().compareTo(other.amount()) < 0) {
            throw new BusinessRuleViolationException("Недостаточно средств");
        }
    }

    /**
     * Проверяет возможность перевода средств на указанный аккаунт.
     * Метод использует {@link #validateDepositAllowed(Money)} для проверки:
     * <ul>
     *     <li>Активность аккаунта получателя</li>
     *     <li>Совпадение валют</li>
     *     <li>Сумма депозита больше нуля</li>
     * </ul>
     *
     * @param account аккаунт получателя
     * @return true если указанный аккаунт активен
     * @throws BusinessRuleViolationException если аккаунта получателя не активен,
     *                                        валюты не совпадают или сумма депозита меньше или ровна нулю
     * @throws CurrencyMismatchException      если валюты не совпадают
     */
    public boolean canTransferTo(Account account) {
        validateDepositAllowed(account.money);
        return true;
    }

    /**
     * Проверяет возможность пополнение средств текущего аккаунта.
     * Метод проверяет:
     * <ul>
     *     <li>Активен ли аккаунт</li>
     *     <li>Совпадает ли валюта</li>
     *     <li>Сумма депозита больше нуля</li>
     * </ul>
     *
     * @param money сумма для пополнения
     * @throws BusinessRuleViolationException если аккаунта получателя не активен,
     *                                        валюты не совпадают или сумма депозита меньше или ровна нулю
     * @throws CurrencyMismatchException      если валюты не совпадают
     */
    private void validateDepositAllowed(Money money) {
        if (!isActive()) {
            throw new BusinessRuleViolationException("Невозможно пополнить средства: аккаунт получателя неактивен");
        }
        checkCurrencyEquality(money);
        if (money.amount().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessRuleViolationException("Сумма депозита должна быть больше нуля");
        }
    }

    /**
     * Проверяет, совпадают ли валюты текущего аккаунта и переданного объекта {@link Money}.
     *
     * @param money объект {@link Money} для сравнения
     * @throws CurrencyMismatchException если валюты не совпадают
     */
    private void checkCurrencyEquality(Money money) {
        if (!this.money.currency().equals(money.currency())) {
            throw new CurrencyMismatchException(
                    String.format("Нельзя выполнить операцию: валюты не совпадают (%s ≠ %s)",
                            this.money.currency(), money.currency())
            );
        }
    }

    /**
     * Проверяет возможность перевода указанной суммы с текущего аккаунта на другой.
     * Метод выполняет следующие проверки:
     * <ul>
     *     <li>Активность целевого аккаунта, совпадение валют и что сумма депозита больше 0({@link #canTransferTo(Account)})</li>
     *     <li>Достаточность средств, совпадение валют и активность текущего аккаунта ({@link #canWithdraw(Money)})</li>
     * </ul>
     * <p>
     * В случае, если хотя бы одна из проверок не проходит, выбрасывается соответствующее исключение:
     * <ul>
     *     <li>{@link BusinessRuleViolationException} если целевой аккаунт неактивен или недостаточно средств на текущем аккаунте.</li>
     *     <li>{@link CurrencyMismatchException} если валюты не совпадают.</li>
     * </ul>
     *
     * @param target аккаунт получателя
     * @param amount сумма для перевода
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
