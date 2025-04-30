package ru.katacademy.bank_app.user.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.katacademy.bank_app.user.domain.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
