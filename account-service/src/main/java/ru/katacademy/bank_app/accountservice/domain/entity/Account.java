package ru.katacademy.bank_app.accountservice.domain.entity;

import lombok.Getter;
import ru.katacademy.bank_app.accountservice.domain.enumtype.AccountStatus;
import ru.katacademy.bank_app.accountservice.infrastructure.persistence.entity.UserEntity;
import ru.katacademy.bank_shared.valueobject.AccountNumber;
import ru.katacademy.bank_shared.valueobject.Money;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Класс, представляющий банковский счет.
 * <p>
 * Содержит информацию о текущем статусе счета и предоставляет методы
 * для управления этим статусом (блокировка, закрытие, проверка активности).
 * </p>
 * <p>
 * Поля:
 * - accountNumber: номер счета
 * - user: пользователь, которому принадлежит аккаунт
 * - balance: баланс счета
 * - status: текущий статус счета. Определяет доступность счета для операций.
 * - createdAt: время создания аккаунта.
 */
@Getter
public class Account {

    private final Long id;
    private final AccountNumber accountNumber;
    private UserEntity user;
    private Money balance;
    private AccountStatus status;
    private final LocalDateTime createdAt;


    public Account(Long id, UserEntity user, AccountNumber accountNumber,
                   Money balance, AccountStatus status, LocalDateTime createdAt) {

        if (balance == null || balance.amount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial balance cannot " +
                    "be null or negative.");
        }
        if (accountNumber == null) {
            throw new IllegalArgumentException("Account number cannot be null.");
        }
        if (status == null) {
            throw new IllegalArgumentException("Account status cannot be null.");
        }
        this.id = id;
        this.user = user;
        this.balance = balance;
        this.accountNumber = accountNumber;
        this.status = status;
        this.createdAt = createdAt;
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
        this.balance = this.balance.add(money);
    }

    /**
     * Метод реализует уменьшение баланса счета на заданную сумму.
     *
     * @param amount сумма денег, которую клиент хочет снять со счета.
     */
    public void withdraw(Money amount) {
        this.balance = this.balance.subtract(amount);
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
        final Account account =
                (Account) o;
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
