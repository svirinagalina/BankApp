package ru.katacademy.bank_shared.valueobject;


import java.io.Serializable;

import java.util.Random;


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

    /**
     * Генерирует временный номер счета - строку состоящую из 20 числовых символов от 0 до 9
     *
     * @return новый объект AccountNumber c
     */
    public static AccountNumber generateAccountNumber() {
        final Random random = new Random();
        final StringBuilder number = new StringBuilder();

        for (int i = 0; i < 20; i++) {
            number.append(random.nextInt(10));
        }

        return new AccountNumber(number.toString());
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
