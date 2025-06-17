package ru.katacademy.bank_app.account.domain.entity;

import lombok.Getter;
import ru.katacademy.bank_app.account.domain.enumtype.AccountStatus;
import ru.katacademy.bank_shared.valueobject.AccountNumber;
import ru.katacademy.bank_shared.valueobject.Money;

import java.math.BigDecimal;
import java.util.Objects;

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
 * <p>Создание объекта осуществляется через фабричный метод {@link #newAccount(AccountNumber, Money, AccountStatus)}.
 * </p>
 * @author Sheffy
 */
@Getter
public final class Account {
    private Money money;
    private AccountStatus status;
    private final AccountNumber accountNumber;

    private Account(AccountNumber accountNumber, Money money, AccountStatus status) {
        this.money = money;
        this.accountNumber = accountNumber;
        this.status = status;
    }

    /**
     * Фабричный метод для создания нового счёта.
     *
     * @param accountNumber номер счёта (не должен быть {@code null})
     * @param money         первоначальный баланс (не может быть {@code null} или отрицательным)
     * @param status        начальный статус счёта (не должен быть {@code null})
     * @return новый экземпляр {@link Account}
     * @throws NullPointerException     если любой из обязательных параметров равен {@code null}
     * @throws IllegalArgumentException если баланс равен {@code null} или отрицательный
     */
    public static Account newAccount(AccountNumber accountNumber, Money money, AccountStatus status) {
        Objects.requireNonNull(accountNumber, "Account number must not be null");
        Objects.requireNonNull(status, "Account status must not be null");
        if (money == null || money.amount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial balance cannot " +
                    "be null or negative.");
        }
        return new Account(accountNumber, money, status);
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
        this.money = this.money.add(money);
    }

    /**
     * Метод реализует уменьшение баланса счета на заданную сумму.
     *
     * @param amount сумма денег, которую клиент хочет снять со счета.
     */
    public void withdraw(Money amount) {
        this.money = this.money.subtract(amount);
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
