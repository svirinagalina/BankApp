package ru.katacademy.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.katacademy.application.dto.LoginAttemptDto;
import ru.katacademy.domain.entity.LoginAttempt;
import ru.katacademy.domain.mapper.LoginAttemptMapper;
import ru.katacademy.domain.repository.LoginAttemptRepository;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/auth-statistics")
public class AuthStatisticsController {
    private final LoginAttemptRepository loginAttemptRepository;
    private final LoginAttemptMapper loginAttemptMapper;

    public AuthStatisticsController(LoginAttemptRepository loginAttemptRepository,
                                    LoginAttemptMapper loginAttemptMapper) {
        this.loginAttemptRepository = loginAttemptRepository;
        this.loginAttemptMapper = loginAttemptMapper;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LoginAttemptDto>> getLoginAttemptHistoryByUser(@PathVariable Long userId) {
        var attempts = loginAttemptRepository.findByUserId(userId);
        var dto = attempts.stream().map(loginAttemptMapper::toDto).toList();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/user/{userId}/filter")
    public ResponseEntity<List<LoginAttemptDto>> getFilteredLoginAttempt(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(required = false) Boolean success) {
        List<LoginAttempt> attempts;
        attempts = getLoginAttempts(userId, start, end, success);
        var dto = attempts.stream()
                .map(loginAttemptMapper::toDto)
                .toList();
        return ResponseEntity.ok(dto);


    }

    private List<LoginAttempt> getLoginAttempts(Long userId, LocalDateTime start, LocalDateTime end, Boolean success) {
        if (start != null && end != null) {
            return loginAttemptRepository.findByTimestamp(start, end);
        }
        if (success != null) {
            return loginAttemptRepository.findBySuccess(success);
        }
        return loginAttemptRepository.findByUserId(userId);
    }
}
