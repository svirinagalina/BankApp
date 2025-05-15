package ru.katacademy.bank_shared.exception;

/**
 * Ошибка валидации формата Email.
 */
public class InvalidEmailException extends RuntimeException {
    /**
     * Создаёт исключение с указанным сообщением.
     *
     * @param message описание причины ошибки
     */
    public InvalidEmailException(String message) {
        super(message);
    }
}
