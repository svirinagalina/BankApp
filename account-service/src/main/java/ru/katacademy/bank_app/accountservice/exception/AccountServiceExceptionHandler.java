package ru.katacademy.bank_app.accountservice.exception;

import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.katacademy.bank_app.accountservice.application.dto.ErrorResponse;
import ru.katacademy.bank_shared.exception.ValidationException;

/**
 * Глобальный обработчик исключений для всего приложения.
 * Обрабатывает ошибки валидации (@Valid) и другие исключения.
 */
@RestControllerAdvice(basePackages = "ru.katacademy.bank_app.accountservice")
public class AccountServiceExceptionHandler {

    /**
     * Обрабатывает ошибки валидации, когда @Valid не проходит.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {

        final List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> String.format("%s: %s",
                        error.getField(),
                        error.getDefaultMessage()))
                .collect(Collectors.toList());

        final ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Ошибка валидации входных данных")
                .errors(errors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Обрабатывает ConstraintViolationException для валидации параметров методов.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex) {

        final List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(violation -> String.format("%s: %s",
                        violation.getPropertyPath().toString(),
                        violation.getMessage()))
                .collect(Collectors.toList());

        final ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Ошибка валидации параметров")
                .errors(errors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Обрабатывает старые ValidationException из ValidationAspect.
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleOldValidationException(
            ValidationException ex) {

        final ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Ошибка валидации")
                .errors(ex.getErrors())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}