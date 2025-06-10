package ru.katacademy.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.katacademy.domain.entity.LoginAttempt;

import java.time.LocalDateTime;
import java.util.List;

public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {
    List<LoginAttempt> findByUserId(Long userId);
    List<LoginAttempt> findByTimestamp(LocalDateTime start, LocalDateTime end);
    List<LoginAttempt> findBySuccess(boolean success);
}
