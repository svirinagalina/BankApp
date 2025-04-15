package ru.katacademy.bank_app.shared.valueobject;

/**
 * Value Object для Email.
 * Валидирует корректность email-адреса при создании.
 *
 * @param value строковое представление email-адреса
 * @throws IllegalArgumentException если email некорректен
 */
public record Email(String value) {
    public Email {
        if (value == null || !value.matches("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email format: " + value);
        }
    }
}
