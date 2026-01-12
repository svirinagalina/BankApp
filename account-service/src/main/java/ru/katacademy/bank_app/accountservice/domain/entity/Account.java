package ru.katacademy.bank_app.accountservice.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import ru.katacademy.bank_app.accountservice.domain.enumtype.AccountStatus;
import ru.katacademy.bank_app.accountservice.infrastructure.persistence.entity.UserEntity;
import ru.katacademy.bank_shared.valueobject.AccountNumber;
import ru.katacademy.bank_shared.valueobject.Money;

/**
 * Класс, представляющий банковский счет.
 */
@Getter
public class Account {

    private final Long id;
    private final AccountNumber accountNumber;
    private UserEntity user;
    private Money balance;
    private AccountStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;  // ← ДОБАВЛЕНО

    public Account(Long id, UserEntity user, AccountNumber accountNumber,
                   Money balance, AccountStatus status,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {  // ← ИЗМЕНЕНО
        if (balance == null || balance.amount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial balance cannot be null or negative.");
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
        if (createdAt != null) {
            this.createdAt = createdAt;
        } else {
            this.createdAt = LocalDateTime.now();
        }
        if (updatedAt != null) {
            this.updatedAt = updatedAt;
        } else {
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Проверяет, является ли счет активным.
     */
    public boolean isActive() {
        return status == AccountStatus.ACTIVE;
    }

    /**
     * Блокирует счет.
     */
    public void blockAccount() {
        status = AccountStatus.BLOCKED;
        updateTimestamp();
    }

    /**
     * Закрывает счет.
     */
    public void closeAccount() {
        status = AccountStatus.CLOSE;
        updateTimestamp();
    }

    /**
     * Пополняет баланс
     */
    public void deposit(Money money) {
        this.balance = this.balance.add(money);
        updateTimestamp();
    }

    /**
     * Метод реализует уменьшение баланса счета на заданную сумму.
     */
    public void withdraw(Money amount) {
        this.balance = this.balance.subtract(amount);
        updateTimestamp();
    }

    /**
     * Обновляет timestamp изменения.
     */
    private void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Проверяет равенство аккаунтов по {@link AccountNumber}.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Account account = (Account) o;
        return accountNumber.equals(account.accountNumber);
    }

    /**
     * Возвращает хэш-код на основе {@link AccountNumber}.
     */
    @Override
    public int hashCode() {
        return accountNumber.hashCode();
    }
}