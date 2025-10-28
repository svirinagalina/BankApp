package ru.katacademy.bank_app.accountservice.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.katacademy.bank_app.accountservice.domain.entity.Account;
import ru.katacademy.bank_app.accountservice.domain.enumtype.AccountStatus;
import ru.katacademy.bank_shared.conventor.AccountNumberConverter;
import ru.katacademy.bank_shared.conventor.MoneyConverter;
import ru.katacademy.bank_shared.valueobject.AccountNumber;
import ru.katacademy.bank_shared.valueobject.Money;

import java.time.LocalDateTime;

/**
 * JPA‑сущность для таблицы «accounts».
 * Соответствует доменному объекту {@link Account}.
 */
@Entity
@Getter
@Setter
@ToString
@Table(name = "accounts")
public class AccountEntity {

    /** Идентификатор записи в БД. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Номер аккаунта. */
    @Convert(converter = AccountNumberConverter.class)
    @Column(name = "account_number", nullable = false, unique = true)
    private AccountNumber accountNumber;

    /** Пользователь, которому принадлежит аккаунт. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    /** Баланс аккаунта. */
    @Convert(converter = MoneyConverter.class)
    @Column(name = "balance", nullable = false)
    private Money balance;

    /** Статус аккаунта. */
    @Column(name = "status", nullable = false)
    private AccountStatus status;

    /** Время создания аккаунта. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public AccountEntity() {
    }

    public AccountEntity(AccountNumber accountNumber, UserEntity user, Money balance, AccountStatus status, LocalDateTime createdAt) {
        this.accountNumber = accountNumber;
        this.user = user;
        this.balance = balance;
        this.status = status;
        this.createdAt = createdAt;
    }
}
