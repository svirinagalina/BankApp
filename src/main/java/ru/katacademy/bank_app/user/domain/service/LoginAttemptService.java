package ru.katacademy.bank_app.user.domain.service;

public interface LoginAttemptService {
    void recordLoginAttempt(Long userId, String email, String ip, String userAgent, boolean success);
}
