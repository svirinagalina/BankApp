package ru.katacademy.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.katacademy.application.dto.LoginAttemptDto;
import ru.katacademy.domain.entity.LoginAttempt;
import ru.katacademy.infrastructure.mapper.LoginAttemptMapper;
import ru.katacademy.domain.repository.LoginAttemptAuthRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST-контроллер для работы со статистикой аутентификации пользователей.
 * <p>
 * Предоставляет API для получения истории попыток входа в систему с возможностью фильтрации.
 * Все методы возвращают данные в формате {@link LoginAttemptDto}.
 * Примечание по использованию var: В коде используется ключевое слово var,
 * которое позволяет объявлять локальные переменные без явного указания типа, когда тип может быть
 * выведен из контекста.
 * </p>
 *
 * @author MihasBatler
 */
@RestController
@RequestMapping("/api/auth-statistics")
public class AuthStatisticsController {
    private final LoginAttemptAuthRepository loginAttemptAuthRepository;
    private final LoginAttemptMapper loginAttemptMapper;

    public AuthStatisticsController(LoginAttemptAuthRepository loginAttemptAuthRepository,
                                    LoginAttemptMapper loginAttemptMapper) {
        this.loginAttemptAuthRepository = loginAttemptAuthRepository;
        this.loginAttemptMapper = loginAttemptMapper;
    }

    /**
     * Получает историю всех попыток входа для указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список {@link LoginAttemptDto} с историей попыток входа
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LoginAttemptDto>> getLoginAttemptHistoryByUser(@PathVariable Long userId) {
        var attempts = loginAttemptAuthRepository.findByUserId(userId);
        var dto = attempts.stream().map(loginAttemptMapper::toDto).toList();
        return ResponseEntity.ok(dto);
    }

    /**
     * Получает отфильтрованную историю попыток входа для пользователя.
     * <p>
     * Осуществляет фильтрацию по:
     * <ul>
     *   <li>Диапазону дат (start и end)</li>
     *   <li>Результату попытки (success)</li>
     * </ul>
     *
     * @param userId  идентификатор пользователя
     * @param start   начальная дата диапазона (в формате ISO-8601)
     * @param end     конечная дата диапазона (в формате ISO-8601)
     * @param success фильтр по результату попытки (true/false)
     * @return список {@link LoginAttemptDto} с отфильтрованными попытками входа
     */
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

    /**
     * Метод для получения попыток входа с учетом фильтров.
     *
     * @param userId  идентификатор пользователя
     * @param start   начальная дата диапазона
     * @param end     конечная дата диапазона
     * @param success фильтр по результату попытки
     * @return список {@link LoginAttempt} с учетом примененных фильтров
     */
    private List<LoginAttempt> getLoginAttempts(Long userId, LocalDateTime start, LocalDateTime end, Boolean success) {
        if (start != null && end != null) {
            return loginAttemptAuthRepository.findByTimestamp(start, end);
        }
        if (success != null) {
            return loginAttemptAuthRepository.findBySuccess(success);
        }
        return loginAttemptAuthRepository.findByUserId(userId);
    }
}
