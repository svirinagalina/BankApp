package ru.katacademy.bank_app.user_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.katacademy.bank_shared.exception.UserNotFoundException;

/** Глобальный обработчик исколючений для Rest-контроллеров.
 *
 * Назначение:
 * Централизованно перехватывает и обрабатывает исключения возникшие в user-service.
 *
 * Методы:
 * - handleUserNotFound(): обрабатывает исключения когда пользователь не найден.
 *
 * Автор: Быстров М.
 * Дата: 2025-05-23
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обрабатывает исключение возникшее при отсутствии пользователя в системе.
     * @param e исключение UserNotFoundException
     * @return HTTP ответ с кодом 404 и сообщением об ошибке
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}
