package ru.katacademy.bank_app.accountservice.exception;

public class AccountNotFoundExceptionResolver extends RuntimeException {
    public AccountNotFoundExceptionResolver(String message) {
        super(message);
    }
}
