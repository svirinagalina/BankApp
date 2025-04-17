package ru.katacademy.bank_app.shared.exception;

/**
 * Исключение, выбрасывается, если пользователь не найден.
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}