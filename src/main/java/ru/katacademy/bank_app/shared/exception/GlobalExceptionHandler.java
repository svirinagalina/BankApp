package ru.katacademy.bank_app.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.security.auth.login.AccountNotFoundException;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Глобальный обработчик исключений для REST API приложения.
 *
 * <p>
 * Этот класс перехватывает указанные исключения, выброшенные в любом месте приложения,
 * и возвращает клиенту структурированный HTTP-ответ в формате JSON.
 * </p>
 *
 * <p>Структура ответа включает:</p>
 * <ul>
 *     <li><b>message</b> — сообщение об ошибке</li>
 *     <li><b>timestamp</b> — дата и время возникновения ошибки</li>
 *     <li><b>status</b> — HTTP-статус</li>
 * </ul>
 *
 * <p>Это позволяет сделать API предсказуемым и удобным для клиента.</p>
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Обрабатывает исключение UserNotFoundException и возвращает HTTP 404 с описанием ошибки.
     *
     * @param e исключение, возникающее при отсутствии пользователя
     * @return ResponseEntity с JSON-сообщением, временем и статусом 404 Not Found
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(UserNotFoundException e) {
        return buildResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Обрабатывает исключение AccountNotFoundException и возвращает HTTP 404 с описанием ошибки.
     *
     * @param e исключение "Аккаунт не найден"
     * @return ResponseEntity с сообщением об ошибке, текущим временем и статусом кода
     */
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleAccountNotFound(AccountNotFoundException e) {
        return buildResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Обрабатывает исключение InsufficientFundsException и возвращает HTTP 400 с описанием ошибки.
     *
     * @param e Исключение, выбрасываемое при попытке снять средства,
     *          если на счёте недостаточно денег.
     * @return ResponseEntity с сообщением об ошибке, текущим временем и статусом кода
     */
    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientFunds(InsufficientFundsException e) {
        return buildResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Обрабатывает исключение ConstraintViolationException, которое возникает при нарушении ограничений
     * валидации
     *
     * @param e исключение, возникающее при нарушении ограничений валидации, например, из-за некорректных данных
     * @return ResponseEntity с сообщением об ошибке, текущим временем и статусом кода
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException e) {
        return buildResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Обрабатывает все исключения, которые не были явно обработаны в других методах обработчиков.
     *
     * @param e исключение, которое не было обработано другими обработчиками. Это может быть любое
     *          непойманное исключение, выброшенное в приложении.
     * @return ResponseEntity с сообщением об ошибке, текущим временем и статусом кода
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        return buildResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Формирует HTTP-ответ в виде JSON-объекта.
     *
     * @param message сообщение об ошибке
     * @param status  HTTP-статус, соответствующий типу ошибки
     * @return ResponseEntity с телом ответа (message, timestamp, status)
     */
    private ResponseEntity<Map<String, Object>> buildResponse(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        return new ResponseEntity<>(response, status);
    }
}
