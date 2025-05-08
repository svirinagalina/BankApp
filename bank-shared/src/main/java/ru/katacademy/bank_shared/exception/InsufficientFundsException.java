package ru.katacademy.bank_shared.exception;

/**
 * Исключение, выбрасываемое при попытке снять средства,
 * если на счёте недостаточно денег.
 */
public class InsufficientFundsException extends RuntimeException {
    /**
     * Создаёт исключение с указанным сообщением.
     *
     * @param message описание причины ошибки
     */
    public InsufficientFundsException(String message) {
        super(message);
    }
}
