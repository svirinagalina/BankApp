package ru.katacademy.bank_shared.exception;

/**
 * Выбрасывается при попытке провести операцию между несовместимыми валютами.
 */
public class CurrencyMismatchException extends RuntimeException {
    public CurrencyMismatchException(String message) {
        super(message);
    }
}
