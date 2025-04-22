package ru.katacademy.bank_app.account.domain.entity;

import lombok.Getter;
import ru.katacademy.bank_app.account.domain.enumtype.AccountStatus;
import ru.katacademy.bank_app.account.domain.enumtype.Currency;
import ru.katacademy.bank_app.shared.valueobject.AccountNumber;
import ru.katacademy.bank_app.shared.valueobject.Money;

import java.math.BigDecimal;

/**
 * Представляет банковский аккаунт с денежным балансом, включающим сумму и валюту.
 * Содержит информацию о текущем статусе счета
 * и предоставляет методы для управления этим статусом (блокировка, закрытие, проверка активности).
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
 * @author Rizvan Gunaev
 */
@Getter
public class Account {
    private Money money;
    private AccountStatus status = AccountStatus.ACTIVE;
    private final Currency currency;
    private final AccountNumber accountNumber;


    public Account(Money money, Currency currency, AccountNumber accountNumber) {
        if (money == null || money.amount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial balance cannot be null or negative.");
        }
        if (currency == null) {
            throw new IllegalArgumentException("Currency cannot be null.");
        }
        if (accountNumber == null) {
            throw new IllegalArgumentException("Account number cannot be null.");
        }
        this.money = money;
        this.currency = currency;
        this.accountNumber = accountNumber;
    }

    /**
     * Создаёт новый банковский аккаунт с заданным номером счёта, начальными средствами и статусом.
     *
     * @param accountNumber уникальный номер счёта
     * @param money         начальный баланс (сумма и валюта)
     * @param status        статус счёта при создании (например, ACTIVE или BLOCKED)
     */
    public Account(AccountNumber accountNumber, Money money, AccountStatus status) {
        this.accountNumber = accountNumber;
        this.money = money;
        this.status = status;
    }

    /**
     * Пополняет баланс на указанную сумму
     * <p>
     * Если аккаунт не активен выбрасывает исключение {@link AccountInactiveException}
     * </p>
     *
     * @param money объект предоставляющий сумму валюты и вид валюты
     * @throws AccountInactiveException если аккаунт неактивен и операция невозможна.
     */
    public void deposit(Money money) {
        if (!isActive()) {
            throw new AccountInactiveException("Невозможно пополнить: аккаунт неактивен.");
        }
        this.money = this.money.add(money);
    }

    /**
     * Вычитает баланс на указанную сумму.
     * <p>
     * Если аккаунт неактивен, выбрасывает исключение {@link AccountInactiveException}.
     * Если на счете недостаточно средств для выполнения операции, выбрасывает исключение
     * {@link InsufficientFundsException}.
     * </p>
     *
     * @param money объект предоставляющий сумму валюты и вид валюты
     * @throws AccountInactiveException   если аккаунт неактивен и операция невозможна.
     * @throws InsufficientFundsException если на счете недостаточно средств для выполнения операции.
     */
    public void withdraw(Money money) {
        if (!isActive()) {
            throw new AccountInactiveException("Невозможно снять средства: аккаунт неактивен.");
        }
        if (this.money.amount().compareTo(money.amount()) < 0) {
            throw new InsufficientFundsException("Недостаточно средств для снятия со счета.");
        }
        this.money = this.money.subtract(money);
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
            throw new IllegalArgumentException("Deposit amount must be greater than zero.");
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
            throw new IllegalStateException("Account is not active. Withdrawal not allowed.");
        }
        if (!amount.currency().equals(this.currency)) {
            throw new IllegalArgumentException("Currency mismatch.");
        }
        this.money = this.money.subtract(amount);
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
