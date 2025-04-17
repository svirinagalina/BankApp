package ru.katacademy.bank_app.account.domain.entity;

import ru.katacademy.bank_app.account.domain.enumtype.AccountStatus;
import ru.katacademy.bank_app.shared.valueobject.AccountNumber;
import ru.katacademy.bank_app.shared.valueobject.Money;
import ru.katacademy.bank_app.shared.exception.InsufficientFundsException;

import java.util.Objects;

/**
 * Представляет банковский аккаунт с денежным балансом, включающим сумму и валюту.
 * Содержит информацию о текущем статусе счета
 * и предоставляет методы для управления этим статусом (блокировка, закрытие, проверка активности).
 * <p>
 * Класс гарантирует что:
 * <ul>
 *     <li>Сумма вычета и сложения не может привести к отрицательному значению</li>
 *     <li>Методы блокировки и закрытия изменяют статус счета, делая его недоступным для операций</li>
 * </ul>
 *
 * @author Rizvan Gunaev
 */
public class Account {
    /**
     * Номер счета текущего аккаунта
     */
    private final AccountNumber accountNumber;
    /**
     * Текущий денежный баланс, содержащий данные о валюте и ее сумме.
     */
    private Money money;
    /**
     * Текущий статус счета, который определяет его доступность для операций.
     */
    private AccountStatus status;

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
     *
     * @param money объект предоставляющий сумму валюты и вид валюты
     */
    public void deposit(Money money) {
        this.money = this.money.add(money);
    }

    /**
     * Вычитает баланс
     * <p>
     * Если на счете недостаточно средств для выполнения операции, выбрасывается исключение
     * {@link InsufficientFundsException}.
     * </p>
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
     * Сравнивает текущий аккаунт с другим объектом на равенство.
     * <p>
     * Сравнение происходит только по {@link AccountNumber},
     * поскольку номер счёта однозначно идентифицирует аккаунт.
     * </p>
     *
     * @param o объект, с которым сравнивается текущий аккаунт
     * @return {@code true}, если объект является {@code Account} и имеет такой же номер счёта;
     * {@code false} в остальных случаях
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account account)) return false;
        return Objects.equals(accountNumber, account.accountNumber);
    }

    /**
     * Возвращает хэш-код аккаунта на основе его номера счёта.
     * <p>
     * Этот метод согласован с {@link #equals(Object)}, то есть
     * если два аккаунта равны по {@code equals}, то их {@code hashCode} также совпадают.
     * </p>
     *
     * @return хэш-код, вычисленный по {@code accountNumber}
     */
    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }
}
