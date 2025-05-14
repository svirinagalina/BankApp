package ru.katacademy.bank_shared.valueobject;

import ru.katacademy.bank_shared.exception.InvalidEmailException;

/**
 * Value Object для Email.
 * Валидирует корректность email-адреса при создании.
 *
 * @param value строковое представление email-адреса
 */
public record Email(String value) {

    private static final String EMAIL_REGEX = "^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$";

    public Email {
        if (!isValid(value)) {
            throw new InvalidEmailException("Invalid email format: " + value);
        }
    }

    /**
     * Проверяет, валиден ли переданный email.
     *
     * @param email email-строка
     * @return true, если формат корректный
     */
    public static boolean isValid(String email) {
        return email != null && email.matches(EMAIL_REGEX);
    }
}
