package ru.katacademy.bank_app.accountservice.domain.service;

public interface LoginAttemptService {
    void recordLoginAttempt(Long userId, String email, String ip, String userAgent, boolean success);
}
