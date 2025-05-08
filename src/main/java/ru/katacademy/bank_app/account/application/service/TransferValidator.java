package ru.katacademy.bank_app.account.application.service;

import org.springframework.stereotype.Service;
import ru.katacademy.bank_app.account.domain.entity.Account;
import ru.katacademy.bank_shared.exception.BusinessRuleViolationException;
import ru.katacademy.bank_shared.valueobject.Money;

import java.math.BigDecimal;

import static ru.katacademy.bank_shared.exception.BusinessErrorCode.*;

@Service
public class TransferValidator {

    /**
     * Проверяет возможность перевода указанной суммы с текущего аккаунта на другой.
     * Метод выполняет следующие проверки:
     * <ul>
     *     <li>Сумма пополнения больше 0 ({@link #canTransferTo(Account, Money)})</li>
     *     <li>Достаточность средств, совпадение валют и активность текущего аккаунта ({@link #canWithdraw(Account from, Money money)})</li>
     * </ul>
     * <p>
     * В случае, если хотя бы одна из проверок не проходит, выбрасывается соответствующее исключение:
     * <ul>
     *     <li>{@link BusinessRuleViolationException} если целевой аккаунт неактивен, валюты не совпадают или недостаточно средств на текущем аккаунте.</li>
     * </ul>
     *
     * @param from   аккаунт отправителя
     * @param target аккаунт получателя
     * @param amount сумма для перевода
     */
    public void validateTransferTo(Account from, Account target, Money amount) {
        canWithdraw(from, amount);
        canTransferTo(target, amount);
    }


    /**
     * Проверяет возможность снятия средств с текущего аккаунта.
     * Метод использует {@link #validateWithdrawalAllowed(Account from, Money money)} для проверки:
     * <ul>
     *     <li>Активен ли аккаунт отправителя</li>
     *     <li>Совпадает ли валюта</li>
     *     <li>Достаточность средств для снятия</li>
     * </ul>
     *
     * @param from  аккаунт отправителя
     * @param money сумма для снятия
     * @return true если аккаунт активен, валюты совпадают и достаточно средств
     * @throws BusinessRuleViolationException если одна из проверок не пройдена
     */
    public boolean canWithdraw(Account from, Money money) {
        validateWithdrawalAllowed(from, money);
        return true;
    }

    /**
     * Проверяет возможность перевода средств на указанный аккаунт.
     * Метод использует {@link #validateDepositAllowed(Account target, Money money)} для проверки:
     * <ul>
     *     <li>Активность аккаунта получателя</li>
     *     <li>Совпадение валют</li>
     *     <li>Сумма депозита больше нуля</li>
     * </ul>
     *
     * @param target аккаунт получателя
     * @param money  сумма для пополнения
     * @return true если указанный аккаунт активен
     * @throws BusinessRuleViolationException если аккаунт получателя не активен,
     *                                        валюты не совпадают или сумма депозита меньше или ровна нулю
     */
    public boolean canTransferTo(Account target, Money money) {
        validateDepositAllowed(target, money);
        return true;
    }

    /**
     * Метод проверяет:
     * <ul>
     *     <li>Активен ли аккаунт отправителя</li>
     *     <li>Совпадает ли валюта</li>
     *     <li>Достаточность средств для снятия</li>
     * </ul>
     *
     * @param from  аккаунт отправителя
     * @param money сумма для снятия
     * @throws BusinessRuleViolationException если результат будет отрицательным
     */
    public void validateWithdrawalAllowed(Account from, Money money) {
        if (!from.isActive()) {
            throw new BusinessRuleViolationException(INACTIVE_ACCOUNT, "Невозможно снять средства: Аккаунт отправителя неактивен");
        }
        checkCurrencyEquality(from.getMoney(), money);
        if (from.getMoney().amount().compareTo(money.amount()) < 0) {
            throw new BusinessRuleViolationException(INSUFFICIENT_FUNDS, "Невозможно снять средства: Недостаточно средств");
        }
    }

    /**
     * Проверяет возможность пополнение средств текущего аккаунта.
     * Метод проверяет:
     * <ul>
     *     <li>Активен ли аккаунт получателя</li>
     *     <li>Совпадает ли валюта</li>
     *     <li>Сумма депозита больше нуля</li>
     * </ul>
     *
     * @param target аккаунт получателя
     * @param money  сумма для пополнения
     * @throws BusinessRuleViolationException если аккаунт получателя неактивен,
     *                                        валюты не совпадают, сумма депозита меньше или ровна нулю
     */
    public void validateDepositAllowed(Account target, Money money) {
        if (!target.isActive()) {
            throw new BusinessRuleViolationException(INACTIVE_ACCOUNT, "Невозможно пополнить средства: Аккаунт получателя неактивен");
        }
        checkCurrencyEquality(target.getMoney(), money);
        if (money.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleViolationException(INVALID_AMOUNT, "Невозможно пополнить средства: Сумма для пополнения должна быть больше нуля");
        }
    }

    /**
     * Проверяет, совпадают ли валюты двух объектов {@link Money}.
     *
     * @param from   сумма отправителя
     * @param target сумма получателя
     * @throws BusinessRuleViolationException если валюты не совпадают
     */
    public void checkCurrencyEquality(Money from, Money target) {
        if (!from.currency().equals(target.currency())) {
            throw new BusinessRuleViolationException(CURRENCY_MISMATCH,
                    String.format("Нельзя выполнить операцию: валюты не совпадают (%s ≠ %s)",
                            from.currency(), target.currency())
            );
        }
    }
}
