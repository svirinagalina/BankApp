package ru.katacademy.bank_shared.valueobject;

import java.math.BigDecimal;

/**
 * Представляет неизменяемый объект денег.
 * <p>
 * Класс гарантирует, что:
 * <ul>
 *   <li>Сумма не может быть null или отрицательной</li>
 *   <li>Операции сложения и вычитания возможны только между одинаковыми валютами</li>
 *   <li>Вычитание не допускает отрицательный результат</li>
 * </ul>
 * </p>
 *
 * @param amount   Денежная сумма
 * @param currency Валюта суммы
 */
public record Money(BigDecimal amount, Currency currency) {

    public Money {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
    }

    /**
     * Проверяет, совпадают ли валюты двух объектов Money
     *
     * @param money объект Money для сравнения
     * @throws IllegalArgumentException если валюты не совпадают
     */
    public void checkCurrencyMatch(Money money) {
        if (!this.currency.equals(money.currency)) {
            throw new IllegalArgumentException("Currency does not match currency.");
        }
    }

    /**
     * Проверяет, допустимо ли вычитание (чтобы результат не был отрицательным).
     *
     * @param other объект Money для сравнения
     * @throws IllegalArgumentException если результат будет отрицательным
     */
    private void checkSubtractionAllowed(Money other) {
        if (this.amount.compareTo(other.amount) < 0) {
            throw new IllegalArgumentException("Resulting amount must not be negative.");
        }
    }

    /**
     * Складывает текущую сумму с другой.
     *
     * @param other объект Money, который нужно прибавить
     * @return новый объект Money с увеличенной суммой
     * @throws IllegalArgumentException если валюты не совпадают
     */
    public Money add(Money other) {
        checkCurrencyMatch(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    /**
     * Вычитает из текущей суммы другую.
     *
     * @param other объект Money, который нужно вычесть
     * @return новый объект Money с уменьшенной суммой
     * @throws IllegalArgumentException если валюты не совпадают или результат будет отрицательным
     */
    public Money subtract(Money other) {
        checkCurrencyMatch(other);
        checkSubtractionAllowed(other);
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    /**
     * Сравнивает два объекта Money, чтобы понять,
     * больше ли одна сумма другой (при условии, что они в одной валюте).
     *
     * @param other объект Money, с которым производится сравнение
     * @return true — если текущий объект Money больше, чем переданный. Иначе - false
     * @throws IllegalArgumentException если валюты не совпадают
     */
    public boolean isGreaterThan(Money other) {
        checkCurrencyMatch(other);
        return this.amount.compareTo(other.amount) > 0;
    }
}
