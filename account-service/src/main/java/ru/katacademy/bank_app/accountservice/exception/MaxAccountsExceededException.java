package ru.katacademy.bank_app.accountservice.exception;

public class MaxAccountsExceededException extends RuntimeException {
    public MaxAccountsExceededException(String message) {
        super(message);
    }
}
