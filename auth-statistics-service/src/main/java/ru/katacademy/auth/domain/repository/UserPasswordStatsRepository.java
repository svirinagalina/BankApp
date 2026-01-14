package ru.katacademy.auth.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.katacademy.auth.domain.entity.UserPasswordStats;

import java.util.Optional;

public interface UserPasswordStatsRepository extends JpaRepository<UserPasswordStats, Long> {
    Optional<UserPasswordStats> findByUserId(Long userId);
}