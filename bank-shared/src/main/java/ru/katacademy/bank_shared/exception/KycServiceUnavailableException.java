package ru.katacademy.bank_shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class KycServiceUnavailableException extends RuntimeException {
    public KycServiceUnavailableException(String message) {
        super(message);
    }
}
