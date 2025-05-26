package ru.katacademy.bank_app.settingsservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.katacademy.bank_shared.exception.UserNotFoundException;

/**
 * Глобальный обработчик исключений для settings-service.
 *
 * Обрабатывает исключения, чтобы не возвращался статус 500 при известных ошибках.
 *
 * Методы:
 * - handleUserNotFound(): обработка ошибки отсутствия настроек
 *
 * Автор: Быстров М.
 * Дата: 2025-05-24
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обработка исключения, когда настройки пользователя не найдены.
     *
     * @param e исключение UserNotFoundException
     * @return 404 с сообщением об ошибке
     */

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}
