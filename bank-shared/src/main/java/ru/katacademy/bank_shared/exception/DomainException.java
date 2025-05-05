package ru.katacademy.bank_shared.exception;

/**
 * Базовое исключение для ошибок бизнес-логики.
 * Все исключения, связанные с нарушением бизнес-правил, должны наследоваться от этого класса.
 */
public abstract class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }
}