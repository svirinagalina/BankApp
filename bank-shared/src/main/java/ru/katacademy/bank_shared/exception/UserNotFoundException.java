package ru.katacademy.bank_shared.exception;

/**
 * Исключение, выбрасывается, если пользователь не найден.
 */
public class UserNotFoundException extends RuntimeException {
    /**
     * Создаёт исключение с указанным сообщением.
     *
     * @param message описание причины ошибки
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}