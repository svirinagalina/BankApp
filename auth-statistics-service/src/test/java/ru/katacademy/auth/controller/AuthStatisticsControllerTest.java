package ru.katacademy.auth.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.katacademy.auth.application.dto.LoginAttemptDto;
import ru.katacademy.auth.controller.AuthStatisticsController;
import ru.katacademy.auth.domain.entity.LoginAttempt;
import ru.katacademy.auth.domain.repository.LoginAttemptAuthRepository;
import ru.katacademy.auth.infrastructure.mapper.LoginAttemptMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthStatisticsControllerTest {

    @Mock
    private LoginAttemptAuthRepository repository;

    @Mock
    private LoginAttemptMapper mapper;

    @InjectMocks
    private AuthStatisticsController controller;

    @Test
    void getLoginAttemptHistoryByUser_shouldReturnOnlyUserAttempts() {
        // Arrange
        Long userId = 1L;
        LocalDateTime now = LocalDateTime.now();

        LoginAttempt attempt = new LoginAttempt();
        attempt.setUserId(userId);
        attempt.setTimestamp(now);
        attempt.setSuccess(true);

        LoginAttemptDto dto = new LoginAttemptDto();
        dto.setUserId(userId);
        dto.setTimestamp(now);
        dto.setSuccess(true);

        when(repository.findByUserId(userId)).thenReturn(List.of(attempt));
        when(mapper.toDto(attempt)).thenReturn(dto);

        // Act
        ResponseEntity<List<LoginAttemptDto>> response = controller.getLoginAttemptHistoryByUser(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(userId, response.getBody().get(0).getUserId());
    }

    @Test
    void getFilteredLoginAttempt_shouldFilterByDate() {
        // Arrange
        Long userId = 1L;
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();

        LoginAttempt attempt = new LoginAttempt();
        attempt.setUserId(userId);
        attempt.setTimestamp(end.minusHours(1));
        attempt.setSuccess(true);

        LoginAttemptDto dto = new LoginAttemptDto();
        dto.setUserId(userId);
        dto.setTimestamp(end.minusHours(1));
        dto.setSuccess(true);

        when(repository.findByUserIdAndTimestampBetween(userId, start, end))
                .thenReturn(List.of(attempt));
        when(mapper.toDto(attempt)).thenReturn(dto);

        // Act
        ResponseEntity<List<LoginAttemptDto>> response = controller.getFilteredLoginAttempt(
                userId, start, end, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getFilteredLoginAttempt_shouldFilterBySuccess() {
        // Arrange
        Long userId = 1L;
        boolean success = true;

        LoginAttempt attempt = new LoginAttempt();
        attempt.setUserId(userId);
        attempt.setTimestamp(LocalDateTime.now());
        attempt.setSuccess(success);

        LoginAttemptDto dto = new LoginAttemptDto();
        dto.setUserId(userId);
        dto.setTimestamp(attempt.getTimestamp());
        dto.setSuccess(success);

        when(repository.findByUserIdAndSuccess(userId, success))
                .thenReturn(List.of(attempt));
        when(mapper.toDto(attempt)).thenReturn(dto);

        // Act
        ResponseEntity<List<LoginAttemptDto>> response = controller.getFilteredLoginAttempt(
                userId, null, null, success);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertTrue(response.getBody().get(0).isSuccess());
    }

    @Test
    void getFilteredLoginAttempt_shouldReturnEmptyListWhenNoData() {
        // Arrange
        Long userId = 1L;
        when(repository.findByUserId(userId)).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<LoginAttemptDto>> response = controller.getLoginAttemptHistoryByUser(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getFilteredLoginAttempt_shouldApplyAllFilters() {
        // Arrange
        Long userId = 1L;
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        boolean success = true;

        LoginAttempt attempt = new LoginAttempt();
        attempt.setUserId(userId);
        attempt.setTimestamp(end.minusHours(1));
        attempt.setSuccess(success);

        LoginAttemptDto dto = new LoginAttemptDto();
        dto.setUserId(userId);
        dto.setTimestamp(attempt.getTimestamp());
        dto.setSuccess(success);

        when(repository.findByUserIdAndTimestampBetweenAndSuccess(userId, start, end, success))
                .thenReturn(List.of(attempt));
        when(mapper.toDto(attempt)).thenReturn(dto);

        // Act
        ResponseEntity<List<LoginAttemptDto>> response = controller.getFilteredLoginAttempt(
                userId, start, end, success);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(userId, response.getBody().get(0).getUserId());
        assertTrue(response.getBody().get(0).isSuccess());
    }
}