package ru.katacademy.bank_app.account.domain.entity;

import lombok.Getter;
import lombok.Setter;
import ru.katacademy.bank_app.account.domain.enumtype.AccountStatus;
import ru.katacademy.bank_app.account.domain.enumtype.Currency;
import ru.katacademy.bank_app.shared.valueobject.AccountNumber;
import ru.katacademy.bank_app.shared.valueobject.Money;

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
@Setter
public class Account {
    private Money money;
    private AccountStatus status = AccountStatus.ACTIVE;
    private final Currency currency;
    private final AccountNumber accountNumber;


    public Account(Money money, Currency currency, AccountNumber accountNumber) {
        this.money = money;
        this.currency = currency;
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
        this.money = this.money.add(money);
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
        ru.katacademy.bank_app.account.domain.entity.Account account =
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
