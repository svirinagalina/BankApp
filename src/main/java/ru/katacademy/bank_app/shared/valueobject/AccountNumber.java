package ru.katacademy.bank_app.shared.valueobject;

import java.io.Serializable;

/**
 * Представляет объект номера счета.
 * <p>
 * Этот класс гарантирует, что номер счета состоит ровно из 20 цифр.
 * При создании объекта проверяется, что строка:
 * <ul>
 *   <li>не равна null,</li>
 *   <li>Содержит ровно 20 символов,</li>
 *   <li>Состоит только из цифр.</li>
 * </ul>
 * Если эти условия не выполнены, выбрасывается {@link IllegalArgumentException}.
 * </p>
 */
public record AccountNumber(String accountNumber) implements Serializable {

    /**
     * Проверяет, что номер счета состоит ровно из 20 цифр.
     * Если это не так, выбрасывается исключение {@link IllegalArgumentException}.
     *
     * @param accountNumber Номер счета, который должен быть строкой длиной ровно 20 символов, содержащей только цифры.
     * @throws IllegalArgumentException Если номер счета некорректен (не 20 символов или содержит нецифровые символы).
     */
    public AccountNumber {
        if (accountNumber == null || accountNumber.length() != 20 || !accountNumber.matches("\\d+")) {
            throw new IllegalArgumentException("Account number must be exactly 20 digits long and contain only digits.");
        }
    }

    //Заглушка
    public static AccountNumber generateAccountNumber() {
        return null;
    }

    /**
     * @return строковое представление номера счёта
     */
    public String value() {
        return accountNumber;
    }

    /**
     * Возвращает строку только с номером счёта
     *
     * @return String строку номера счета
     */
    @Override
    public String toString() {
        return accountNumber;
    }
}
