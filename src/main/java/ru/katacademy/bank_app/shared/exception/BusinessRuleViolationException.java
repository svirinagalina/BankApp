package ru.katacademy.bank_app.shared.exception;

/**
 * Исключение выбрасываемое при нарушении бизнес-правила.
 * Например, может быть использовано для случаев, когда операция не может быть выполнена
 * из-за нарушения инварианта, такого как недостаток средств или неактивный аккаунт.
 */
public class BusinessRuleViolationException extends RuntimeException {
    /**
     * Создаёт исключение с указанным сообщением.
     *
     * @param message описание причины ошибки
     */
    public BusinessRuleViolationException(String message) {
        super(message);
    }
}
