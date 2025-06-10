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
        List<LoginAttempt> attempts = loginAttemptRepository.findByUserId(userId);
        List<LoginAttemptDto> dto = attempts.stream().map(loginAttemptMapper::toDto).toList();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/user/{userId}/filter")
    public ResponseEntity<List<LoginAttemptDto>> getFilteredLoginAttempt(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime end,
            @RequestParam(required = false) Boolean success) {
        List<LoginAttempt> attempts;
        if (start != null && end != null) {
            attempts = loginAttemptRepository.findByTimestamp(start, end);
        } else if (success != null) {
            attempts = loginAttemptRepository.findBySuccess(success);
        } else {
            attempts = loginAttemptRepository.findByUserId(userId);
        }
        List<LoginAttemptDto> dto = attempts.stream().map(loginAttemptMapper::toDto).toList();
        return ResponseEntity.ok(dto);
    }
}
