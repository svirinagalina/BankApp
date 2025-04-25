package ru.katacademy.bank_app.shared.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
 *     <li><b>timestamp</b> — время возникновения ошибки</li>
 *     <li><b>status</b> — HTTP-статус (число)</li>
 *     <li><b>error</b> — текстовое описание статуса</li>
 *     <li><b>message</b> — сообщение об ошибке</li>
 *     <li><b>path</b> — путь, по которому произошла ошибка</li>
 * </ul>
 *
 * <p>Это позволяет сделать API предсказуемым и удобным для клиента.</p>
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обрабатывает исключение EmailAlreadyTakenException и возвращает HTTP 409 (Conflict).
     *
     * @param e исключение, возникающее при попытке зарегистрировать уже существующий email
     * @return ResponseEntity с сообщением об ошибке, текущим временем и статусом 409 Conflict
     */
    @ExceptionHandler(EmailAlreadyTakenException.class)
    public ResponseEntity<Map<String, Object>> handleEmailAlreadyTaken(EmailAlreadyTakenException e, HttpServletRequest request) {
        return buildResponse(e.getMessage(), HttpStatus.CONFLICT, request);
    }

    /**
     * Обрабатывает исключение MethodArgumentNotValidException, возникающее при провале валидации
     * данных из тела запроса (@Valid).
     *
     * @param e исключение, содержащее информацию о недопустимых аргументах запроса
     * @return ResponseEntity с первым сообщением об ошибке, текущим временем и статусом 400 Bad Request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        final String message = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");
        return buildResponse(message, HttpStatus.BAD_REQUEST, request);
    }

    /**
     * Формирует HTTP-ответ в виде JSON-объекта.
     *
     * @param message сообщение об ошибке
     * @param status  HTTP-статус, соответствующий типу ошибки
     * @return ResponseEntity с телом ответа (message, timestamp, status, error, path)
     */
    private ResponseEntity<Map<String, Object>> buildResponse(String message, HttpStatus status, HttpServletRequest request) {
        final Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", status.getReasonPhrase());
        response.put("message", message);
        response.put("path", request.getRequestURI());
        return new ResponseEntity<>(response, status);
    }
}