package ru.katacademy.bank_app.accountservice.application.service;

import org.springframework.stereotype.Service;
import ru.katacademy.bank_app.accountservice.domain.entity.LoginAttemptEntry;
import ru.katacademy.bank_app.accountservice.domain.repository.LoginAttemptRepository;
import ru.katacademy.bank_app.accountservice.domain.service.LoginAttemptService;



import java.time.LocalDateTime;

@Service
public class LoginAttemptServiceImpl implements LoginAttemptService {

    private final LoginAttemptRepository loginAttemptRepository;

    public LoginAttemptServiceImpl(LoginAttemptRepository loginAttemptRepository) {
        this.loginAttemptRepository = loginAttemptRepository;
    }

    /**
     * Регистрирует попытку входа пользователя в систему.
     * <p>
     * Этот метод создает запись о попытке входа, включая информацию о пользователе,
     * его IP-адресе, типе пользовательского агента и статусе успеха. Также автоматически
     * присваивается текущая временная метка для каждой попытки входа.
     * </p>
     *
     * @param userId   Идентификатор пользователя, пытающегося войти в систему.
     * @param email    Электронная почта пользователя, связанная с попыткой входа.
     * @param ip       IP-адрес, с которого была осуществлена попытка входа.
     * @param userAgent Информация о браузере или устройстве пользователя,
     *                  отправленная в заголовках HTTP.
     * @param success  Флаг, указывающий, была ли попытка входа успешной
     *                 (true) или нет (false).
     *
     * Автор: Колпаков А.С..
     * Дата: 2025-05-05
     */
    public void recordLoginAttempt(Long userId, String email, String ip, String userAgent, boolean success) {
        final LoginAttemptEntry entry = new LoginAttemptEntry(
                userId,
                email,
                ip,
                userAgent,
                LocalDateTime.now(),
                success
        );
        loginAttemptRepository.save(entry);
    }
}
