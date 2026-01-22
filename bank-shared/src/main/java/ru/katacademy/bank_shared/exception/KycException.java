package ru.katacademy.bank_shared.exception;


public class KycException extends DomainException {
    public KycException(String message) {
        super(message);
    }
}
