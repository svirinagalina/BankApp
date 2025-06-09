package ru.katacademy.bank_shared.event.notification;

public class TransferCompletedEvent {
    private String username;
    private String amount;
    private String recipient;

    public TransferCompletedEvent() {
    }

    public String getUsername() {
        return username;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getAmount() {
        return amount;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
