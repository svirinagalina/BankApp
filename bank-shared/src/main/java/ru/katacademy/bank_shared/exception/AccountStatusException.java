package ru.katacademy.bank_shared.exception;

/**
 * Исключение, выбрасываемое при некорректном статусе банковского аккаунта.
 */
public class AccountStatusException extends RuntimeException {
    public AccountStatusException(String message) {
        super(message);
    }
}
