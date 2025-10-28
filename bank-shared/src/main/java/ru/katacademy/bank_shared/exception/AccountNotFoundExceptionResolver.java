package ru.katacademy.bank_shared.exception;

public class AccountNotFoundExceptionResolver extends RuntimeException {
    public AccountNotFoundExceptionResolver(String message) {
        super(message);
    }
}
