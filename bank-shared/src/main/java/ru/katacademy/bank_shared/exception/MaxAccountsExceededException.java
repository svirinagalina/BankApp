package ru.katacademy.bank_shared.exception;

public class MaxAccountsExceededException extends RuntimeException {
    public MaxAccountsExceededException(String message) {
        super(message);
    }
}
