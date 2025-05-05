package ru.katacademy.bank_app.user.application.service;

public interface LoginAttemptService {
    void recordLoginAttempt(Long userId, String email, String ip, String userAgent, boolean success);
}
