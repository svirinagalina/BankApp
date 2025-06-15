package ru.katacademy.bank_shared.event.notification;

/**
 * Событие, представляющее завершённый денежный перевод.
 * Используется для отправки уведомления через Kafka.
 */
public class TransferCompletedEvent {
    private String username;
    private String amount;
    private String recipient;

    public TransferCompletedEvent() {
    }

    /**
     * Возвращает имя отправителя.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Устанавливает имя отправителя.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Возвращает сумму перевода.
     */
    public String getAmount() {
        return amount;
    }

    /**
     * Устанавливает сумму перевода.
     */
    public void setAmount(String amount) {
        this.amount = amount;
    }

    /**
     * Возвращает имя получателя перевода.
     */
    public String getRecipient() {
        return recipient;
    }

    /**
     * Устанавливает имя получателя перевода.
     */
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
}

