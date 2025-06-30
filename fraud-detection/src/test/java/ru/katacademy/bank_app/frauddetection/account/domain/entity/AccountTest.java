package ru.katacademy.bank_app.frauddetection.account.domain.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.katacademy.bank_app.frauddetection.account.domain.enumtype.AccountStatus;
import ru.katacademy.bank_shared.valueobject.AccountNumber;
import ru.katacademy.bank_shared.valueobject.Currency;
import ru.katacademy.bank_shared.valueobject.Money;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовый класс для {@link Account} - сущности банковского счета.
 * Проверяет основные операции со счетом: блокировку, закрытие,
 * депозиты/снятия средств, а также equals/hashCode.
 */
class AccountTest {

    private Account account;
    private final AccountNumber testNumber = new AccountNumber("12345678901234567890");
    private final Currency rubCurrency = new Currency("RUB", "Russian Ruble", 2);
    private final Money initialBalance = new Money(BigDecimal.valueOf(1000), rubCurrency);

    /**
     * Инициализация тестового счета перед каждым тестом.
     * Создает активный счет с начальным балансом 1000 RUB.
     */
    @BeforeEach
    void setUp() {
        account = new Account(testNumber, initialBalance, AccountStatus.ACTIVE);
    }

    /**
     * Тест проверки активности счета.
     * Проверяет метод isActive() для разных состояний счета.
     */
    @Test
    void isActive() {
        assertTrue(account.isActive());
        account.blockAccount();
        assertFalse(account.isActive());
    }

    /**
     * Тест блокировки счета.
     * Проверяет изменение статуса на BLOCKED после блокировки.
     */
    @Test
    void blockAccount() {
        account.blockAccount();
        assertEquals(AccountStatus.BLOCKED, account.getStatus());
    }

    /**
     * Тест закрытия счета.
     * Проверяет изменение статуса на CLOSE после закрытия.
     */
    @Test
    void closeAccount() {
        account.closeAccount();
        assertEquals(AccountStatus.CLOSE, account.getStatus());
    }

    /**
     * Тест пополнения счета.
     * Проверяет корректность увеличения баланса при депозите.
     */
    @Test
    void deposit() {
        final Money depositAmount = new Money(BigDecimal.valueOf(500), rubCurrency);
        account.deposit(depositAmount);
        assertEquals(new Money(BigDecimal.valueOf(1500), rubCurrency), account.getMoney());
    }

    /**
     * Тест снятия средств со счета.
     * Проверяет корректность уменьшения баланса при снятии.
     */
    @Test
    void withdraw() {
        final Money withdrawAmount = new Money(BigDecimal.valueOf(300), rubCurrency);
        account.withdraw(withdrawAmount);
        assertEquals(new Money(BigDecimal.valueOf(700), rubCurrency), account.getMoney());
    }

    /**
     * Тест сравнения счетов.
     * Проверяет что счета считаются равными при одинаковых номерах.
     */
    @Test
    void testEquals() {
        final Account sameAccount = new Account(testNumber, initialBalance, AccountStatus.ACTIVE);
        final Account differentAccount = new Account(new AccountNumber("09876543210987654321"),
                new Money(BigDecimal.valueOf(1000), rubCurrency), AccountStatus.ACTIVE);

        assertEquals(account, sameAccount);
        assertNotEquals(account, differentAccount);
    }

    /**
     * Тест хэш-кода счета.
     * Проверяет что одинаковые счета имеют одинаковый хэш-код.
     */
    @Test
    void testHashCode() {
        final Account sameAccount = new Account(testNumber, initialBalance, AccountStatus.ACTIVE);
        assertEquals(account.hashCode(), sameAccount.hashCode());
    }

    /**
     * Тест получения текущего баланса.
     * Проверяет корректность возвращаемого значения.
     */
    @Test
    void getMoney() {
        assertEquals(initialBalance, account.getMoney());
    }

    /**
     * Тест получения статуса счета.
     * Проверяет что возвращается правильный статус.
     */
    @Test
    void getStatus() {
        assertEquals(AccountStatus.ACTIVE, account.getStatus());
    }

    /**
     * Тест получения номера счета.
     * Проверяет что возвращается правильный номер счета.
     */
    @Test
    void getAccountNumber() {
        assertEquals(testNumber, account.getAccountNumber());
    }
}