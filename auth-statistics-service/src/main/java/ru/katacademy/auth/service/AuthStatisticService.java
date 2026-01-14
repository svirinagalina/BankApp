package ru.katacademy.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.katacademy.auth.domain.entity.UserPasswordStats;
import ru.katacademy.auth.domain.repository.UserPasswordStatsRepository;
import ru.katacademy.bank.events.password.v1.PasswordChangedEvent;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthStatisticService {

    private final UserPasswordStatsRepository repository;

    /**
     * Обновляет агрегированную статистику по смене пароля.
     * @param event Avro-событие смены пароля
     */
    public void handlePasswordChangedEvent(PasswordChangedEvent event) {
        Long userId = Long.valueOf(event.getUserId());

        UserPasswordStats stats = repository.findByUserId(userId)
                .orElse(new UserPasswordStats(userId, 0L, null));

        stats.setPasswordChangeCount(stats.getPasswordChangeCount() + 1);
        stats.setLastPasswordChange(Instant.ofEpochMilli(event.getOccurredAt()));

        repository.save(stats);
    }
}