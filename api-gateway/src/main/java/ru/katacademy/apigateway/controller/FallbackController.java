package ru.katacademy.apigateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Контроллер, отвечающий за обработку fallback-запросов от Circuit Breaker.
 *
 * <p>Когда целевой микросервис недоступен (например, упал, не отвечает по сети
 * или возвращает ошибку 5xx), в Spring Cloud Gateway срабатывает фильтр CircuitBreaker.
 * Он перенаправляет запрос на специальный внутренний маршрут:
 * <pre>
 *   fallbackUri: forward:/fallback/{serviceName}
 * </pre>
 * Этот контроллер перехватывает такие fallback-вызовы и возвращает
 * унифицированный ответ в формате JSON.
 *
 * <p>Таким образом, FallbackController:
 * <ul>
 *     <li>Предотвращает HTTP-500 ошибки на уровне Gateway;</li>
 *     <li>Возвращает понятный клиенту ответ (503 Service Unavailable);</li>
 *     <li>Дает пользователю информацию, какой именно сервис временно недоступен.</li>
 * </ul>
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping("/account")
    public ResponseEntity<Map<String, Object>> accountFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "status", "FAILED",
                        "service", "account-service",
                        "message", "Service temporarily unavailable. This is a gateway fallback response."
                ));
    }

    @RequestMapping("/security")
    public ResponseEntity<Map<String, Object>> securityFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "status", "FAILED",
                        "service", "security-service",
                        "message", "Service temporarily unavailable. This is a gateway fallback response."
                ));
    }

    @RequestMapping("/notification")
    public ResponseEntity<Map<String, Object>> notificationFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "status", "FAILED",
                        "service", "notification-service",
                        "message", "Service temporarily unavailable. This is a gateway fallback response."
                ));
    }

    @RequestMapping("/fraud")
    public ResponseEntity<Map<String, Object>> fraudFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "status", "FAILED",
                        "service", "fraud-detection",
                        "message", "Service temporarily unavailable. This is a gateway fallback response."
                ));
    }

    @RequestMapping("/audit")
    public ResponseEntity<Map<String, Object>> auditFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "status", "FAILED",
                        "service", "audit-service",
                        "message", "Service temporarily unavailable. This is a gateway fallback response."
                ));
    }

    @RequestMapping("/kyc")
    public ResponseEntity<Map<String, Object>> kycFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "status", "FAILED",
                        "service", "kyc-service",
                        "message", "Service temporarily unavailable. This is a gateway fallback response."
                ));
    }

    @RequestMapping("/settings")
    public ResponseEntity<Map<String, Object>> settingsFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "status", "FAILED",
                        "service", "settings-service",
                        "message", "Service temporarily unavailable. This is a gateway fallback response."
                ));
    }
}

