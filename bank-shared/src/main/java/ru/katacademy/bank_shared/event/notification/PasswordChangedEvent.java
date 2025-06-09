package ru.katacademy.bank_shared.event.notification;


public class PasswordChangedEvent {
    private String username;

    public PasswordChangedEvent() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
