package ru.katacademy.bank_shared.exception;

/**
 * Исключение выбрасываемое при нарушении бизнес-правила.
 * Например, может быть использовано для случаев, когда операция не может быть выполнена
 * из-за нарушения инварианта, такого как недостаток средств или неактивный аккаунт.
 */
public class BusinessRuleViolationException extends RuntimeException {

    private final BusinessErrorCode errorCode;
    private final String detailedMessage;

    /**
     * Создаёт исключение с кодом ошибки и подробным сообщением.
     *
     * @param errorCode код бизнес-ошибки
     * @param detailedMessage подробное описание причины ошибки
     */
    public BusinessRuleViolationException(BusinessErrorCode errorCode, String detailedMessage) {
        super("[" + errorCode + "] " + detailedMessage);
        this.errorCode = errorCode;
        this.detailedMessage = detailedMessage;
    }

    public BusinessErrorCode getErrorCode() {
        return errorCode;
    }

    public String getDetailedMessage() {
        return detailedMessage;
    }
}
