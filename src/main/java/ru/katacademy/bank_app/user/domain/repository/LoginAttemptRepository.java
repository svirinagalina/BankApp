package ru.katacademy.bank_app.user.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.katacademy.bank_app.user.domain.entity.LoginAttemptEntry;

import java.util.List;

public interface LoginAttemptRepository extends JpaRepository<LoginAttemptEntry, Long> {
    List<LoginAttemptEntry> findByUserId(Long userId);
    List<LoginAttemptEntry> findByEmail(String email);
    List<LoginAttemptEntry> findByIp(String ip);
}