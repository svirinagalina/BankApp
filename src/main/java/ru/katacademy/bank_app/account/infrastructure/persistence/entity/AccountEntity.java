package ru.katacademy.bank_app.account.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ru.katacademy.bank_app.account.domain.enumtype.AccountStatus;
import ru.katacademy.bank_shared.conventor.MoneyConverter;
import ru.katacademy.bank_shared.valueobject.AccountNumber;
import ru.katacademy.bank_shared.valueobject.Money;

/**
 * Сущность банковского счета для работы с БД.
 * Хранит основные данные счета: номер, баланс и статус.
 *
 * @author Sheffy
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Entity
public class AccountEntity {
    /** Уникальный номер счета */
    @Id
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "account_number", length = 20)
    private AccountNumber accountNumber;

    /** Текущий баланс счета */
    @Setter
    @Column(nullable = false)
    @Convert(converter = MoneyConverter.class)
    private Money money;

    /** Текущий статус счета */
    @Setter
    @Column(nullable = false)
    private AccountStatus status;
}


