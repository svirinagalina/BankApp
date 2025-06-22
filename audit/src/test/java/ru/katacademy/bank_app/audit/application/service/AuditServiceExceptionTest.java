package ru.katacademy.bank_app.audit.application.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Тестовый класс для {@link AuditServiceException}.
 */
class AuditServiceExceptionTest {

    /**
     * Тест проверяет создание нового исключения с указанным сообщением.
     */
    @Test
    void shouldCreateWithMessage() {
        final String message = "Test message";
        final AuditServiceException exception = new AuditServiceException(message);
        assertEquals(message, exception.getMessage());
    }

    /**
     * Тест проверяет создание нового исключения с указанным сообщением и исходной причиной.
     */
    @Test
    void shouldCreateWithMessageAndCause() {
        final String message = "Test message";
        final Throwable cause = new RuntimeException();
        final AuditServiceException exception = new AuditServiceException(message, cause);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}