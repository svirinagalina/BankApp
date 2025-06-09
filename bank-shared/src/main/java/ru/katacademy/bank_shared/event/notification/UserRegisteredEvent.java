package ru.katacademy.bank_shared.event.notification;

public class UserRegisteredEvent {
    private String username;

    public UserRegisteredEvent() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
