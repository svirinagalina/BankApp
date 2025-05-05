package ru.katacademy.bank_shared.exception;

/**
 * Исключение, выбрасываемое при попытке регистрации пользователя с уже занятым email.
 */
public class EmailAlreadyTakenException extends DomainException {
    public EmailAlreadyTakenException(String email) {
        super("Email уже занят: " + email);
    }
}