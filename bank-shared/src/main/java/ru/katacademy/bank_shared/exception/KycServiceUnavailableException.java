package ru.katacademy.bank_shared.exception;


public class KycServiceUnavailableException extends RuntimeException {
    public KycServiceUnavailableException(String message) {
        super(message);
    }
}
