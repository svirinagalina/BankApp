package ru.katacademy.bank_app.audit.application.service;

/**
 * Исключение, выбрасываемое в случае ошибок при работе AuditService.
 * <p>
 * Автор: Maxim4212
 * Дата: 2025-05-10
 */
public class AuditServiceException extends RuntimeException {
    /**
     * Создаёт новое исключение с указанным сообщением.
     *
     * @param message описание ошибки
     */
    public AuditServiceException(String message) {
        super(message);
    }

    /**
     * Создаёт новое исключение с указанным сообщением и исходной причиной.
     *
     * @param message описание ошибки
     * @param cause   исходное исключение, вызвавшее ошибку
     */
    public AuditServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
