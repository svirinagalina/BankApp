package ru.katacademy.bank_app.accountservice.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.katacademy.bank_app.accountservice.domain.entity.LoginAttemptEntry;
import ru.katacademy.bank_app.accountservice.domain.repository.LoginAttemptRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

// Тест проверяет, что репозиторий вызывается и передаются правильные значения
@ExtendWith(MockitoExtension.class)
public class LoginAttemptServiceImplTest {
    @Mock
    private LoginAttemptRepository loginAttemptRepository;

    @InjectMocks
    private LoginAttemptServiceImpl loginAttemptService;

    @Test
    void recordLoginAttempt_ShouldSaveEntryWithCorrectData() {
        // given
        Long userId = 1L;
        String email = "test@mail.com";
        String ip = "127.0.0.1";
        String userAgent = "Mozilla/5.0";
        boolean success = true;

        // when
        loginAttemptService.recordLoginAttempt(userId, email, ip, userAgent, success);

        // then
        ArgumentCaptor<LoginAttemptEntry> captor = ArgumentCaptor.forClass(LoginAttemptEntry.class);
        verify(loginAttemptRepository, times(1)).save(captor.capture());

        LoginAttemptEntry captured = captor.getValue();
        assertThat(captured.getUserId()).isEqualTo(userId);
        assertThat(captured.getEmail()).isEqualTo(email);
        assertThat(captured.getIp()).isEqualTo(ip);
        assertThat(captured.getUserAgent()).isEqualTo(userAgent);
        assertThat(captured.isSuccess()).isTrue();
        assertThat(captured.getTimestamp()).isBeforeOrEqualTo(LocalDateTime.now());
    }
}
