package ru.katacademy.bank_app.accountservice.exception;

import ru.katacademy.bank_shared.exception.DomainException;

public class KycException extends DomainException {
    public KycException(String message) {
        super(message);
    }
}
