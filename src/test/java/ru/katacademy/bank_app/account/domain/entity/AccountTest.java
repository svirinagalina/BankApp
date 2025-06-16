package ru.katacademy.bank_app.account.domain.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.katacademy.bank_app.account.application.service.TransferValidator;
import ru.katacademy.bank_app.account.domain.enumtype.AccountStatus;
import ru.katacademy.bank_shared.exception.BusinessRuleViolationException;
import ru.katacademy.bank_shared.valueobject.AccountNumber;
import ru.katacademy.bank_shared.valueobject.Money;
import ru.katacademy.bank_shared.valueobject.Currency;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Набор тестов для класса {@link Account}, проверяющих поведение операций
 * пополнения, снятия средств, блокировки, закрытия и валидации перевода.
 * <p>
 * Поля:
 * - sourceAccount: счет, с которого выполняются действия в тестах (счет-отправитель).
 * - targetAccount: счет, на который могут переводиться средства (счет-получатель).
 * - amount: сумма денежных средств, используемая в тестах для проведения операций.
 * - bigAmount: сумма денежных средств, превышающая доступный баланс, используется для тестов ошибок.
 * - transferValidator: вспомогательный класс для проверки корректности перевода между счетами
 * - currency: валюта, используемая во всех тестах.
 * <p>
 * Автор: Maxim4212
 * Дата: 2025-05-11
 */
public class AccountTest {

    private Account sourceAccount;
    private Account targetAccount;
    private Money amount, bigAmount;
    private TransferValidator transferValidator;

    /**
     * Инициализирует данные перед каждым тестом
     */
    @BeforeEach
    void setUp() {
        final Currency currency = new Currency("USD", "Доллар США", 2);
        sourceAccount = Account.newAccount(
                new AccountNumber("01234567899876543210"),
                new Money(new BigDecimal("100.00"), currency),
                AccountStatus.ACTIVE);
        targetAccount = Account.newAccount(
                new AccountNumber("98765432100123456789"),
                new Money(new BigDecimal("50.00"), currency),
                AccountStatus.ACTIVE);
        amount = new Money(new BigDecimal("50.00"), currency);
        bigAmount = new Money(new BigDecimal("150.00"), currency);
        transferValidator = new TransferValidator();
    }

    /**
     * Проверяет, что при пополнении счета баланс увеличивается корректно.
     */
    @Test
    void testDeposit() {
        sourceAccount.deposit(amount);
        assertEquals(new BigDecimal("150.00"), sourceAccount.getMoney().amount());
    }

    /**
     * Проверяет, что при достаточном балансе списание проходит корректно.
     */
    @Test
    void testWithdraw() {
        sourceAccount.withdraw(amount);
        assertEquals(new BigDecimal("50.00"), sourceAccount.getMoney().amount());
    }

    /**
     * Проверяет, что при попытке списать больше средств, чем есть на счете, выбрасывается исключение.
     */
    @Test
    void testWithdraw_InsufficientFunds() {
        assertThrows(IllegalArgumentException.class, () -> sourceAccount.withdraw(bigAmount));
    }

    /**
     * Проверяет, что блокировка аккаунта меняет его статус на BLOCKED.
     */
    @Test
    void testBlockAccount() {
        sourceAccount.blockAccount();
        assertEquals(AccountStatus.BLOCKED, sourceAccount.getStatus());
    }

    /**
     * Проверяет, что закрытие аккаунта меняет его статус на CLOSE.
     */
    @Test
    void testCloseAccount() {
        sourceAccount.closeAccount();
        assertEquals(AccountStatus.CLOSE, sourceAccount.getStatus());
    }

    /**
     * Проверяет, что метод isActive возвращает true для активного аккаунта.
     */
    @Test
    void testIsActive() {
        assertTrue(sourceAccount.isActive());
        sourceAccount.closeAccount();
        assertFalse(sourceAccount.isActive());
    }

    /**
     * Проверяет, что валидация перевода проходит успешно при корректных данных.
     */
    @Test
    void testValidateTransferTo_CorrectTransfer() {
        assertDoesNotThrow(() ->
                transferValidator.validateTransferTo(sourceAccount, targetAccount, amount));
    }

    /**
     * Проверяет, что валидация перевода выбрасывает исключение, если счет-отправитель заблокирован.
     */
    @Test
    void testValidateTransferTo_AccountSenderBlocked() {
        sourceAccount.blockAccount();
        assertThrows(BusinessRuleViolationException.class, () ->
                transferValidator.validateTransferTo(sourceAccount, targetAccount, amount));
    }

    /**
     * Проверяет, что валидация перевода выбрасывает исключение, если счет-отправитель закрыт.
     */
    @Test
    void testValidateTransferTo_AccountSenderClose() {
        sourceAccount.closeAccount();
        assertThrows(BusinessRuleViolationException.class, () ->
                transferValidator.validateTransferTo(sourceAccount, targetAccount, amount));
    }

    /**
     * Проверяет, что валидация перевода выбрасывает исключение, если счет-получатель заблокирован.
     */
    @Test
    void testValidateTransferTo_AccountRecipientBlocked() {
        targetAccount.blockAccount();
        assertThrows(BusinessRuleViolationException.class, () ->
                transferValidator.validateTransferTo(sourceAccount, targetAccount, amount));
    }

    /**
     * Проверяет, что валидация перевода выбрасывает исключение, если счет-получатель закрыт.
     */
    @Test
    void testValidateTransferTo_AccountRecipientClose() {
        targetAccount.closeAccount();
        assertThrows(BusinessRuleViolationException.class, () ->
                transferValidator.validateTransferTo(sourceAccount, targetAccount, amount));
    }

    /**
     * Проверяет, что валидация перевода выбрасывает исключение, если недостаточно средств.
     */
    @Test
    void testValidateTransferTo_InsufficientFunds() {
        assertThrows(BusinessRuleViolationException.class, () ->
                transferValidator.validateTransferTo(sourceAccount, targetAccount, bigAmount));
    }
}
