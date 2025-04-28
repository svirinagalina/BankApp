package ru.katacademy.bank_app.shared.exception;

/**
 * Выбрасывается при попытке провести операцию между несовместимыми валютами.
 */
public class CurrencyMismatchException extends RuntimeException {
    public CurrencyMismatchException(String message) {
        super(message);
    }
}
