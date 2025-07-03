package ru.katacademy.bank_shared.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
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
     * данных из тела запроса, когда аннотированы параметры метода контроллера аннотацией @Valid.
     * Этот метод перехватывает исключение, которое возникает, если входные данные, переданные в
     * контроллер, не соответствуют правилам валидации. Обычно это может происходить при
     * несоответствии значений параметров запроса или тела запроса, которые должны проходить валидацию.
     *
     * @param ex исключение, содержащее информацию о недопустимых аргументах запроса.
     *           Содержит объект BindingResult, который хранит все ошибки валидации.
     * @return ResponseEntity с сообщением об ошибке валидации, текущим временем и статусом 400 Bad Request.
     * В ответе будет передан список ошибок, сгенерированных в процессе валидации.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        final List<String> messages = ex.getBindingResult().getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        Map<String, Object> response = new HashMap<>();
        response.put("messages", messages);
        return ResponseEntity.badRequest().body(response);
    }


    /**
     * Обрабатывает исключение InvalidEmailException и возвращает HTTP 400 Bad Request.
     *
     * @param e исключение, возникающее при попытке зарегистрировать некорректный email
     * @return ResponseEntity с сообщением об ошибке, текущим временем и статусом 400 Bad Request
     */
    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidEmailException(InvalidEmailException e, HttpServletRequest request) {
        return buildResponse(e.getMessage(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(ValidationException ex) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", HttpStatus.BAD_REQUEST.value());
        responseBody.put("errors", ex.getErrors());
        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }


    /**
     * Обрабатывает исключение UserNotFoundException и возвращает HTTP 404 Not Found.
     *
     * @param e исключение, возникающее при попытке найти несуществующего юзера
     * @return ResponseEntity с сообщением об ошибке, текущим временем и статусом 404 Not Found
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFoundException(UserNotFoundException e, HttpServletRequest request) {
        return buildResponse(e.getMessage(), HttpStatus.NOT_FOUND, request);
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