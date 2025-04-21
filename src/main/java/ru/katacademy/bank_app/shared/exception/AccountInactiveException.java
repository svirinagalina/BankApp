package ru.katacademy.bank_app.shared.exception;

/**
 * Исключение выбрасывается при попытке провести операцию с неактивным аккаунтом
 */
public class AccountInactiveException extends RuntimeException {
    /**
     * Создаёт исключение с указанным сообщением.
     *
     * @param message описание причины ошибки
     */
    public AccountInactiveException(String message) {
        super(message);
    }
}
