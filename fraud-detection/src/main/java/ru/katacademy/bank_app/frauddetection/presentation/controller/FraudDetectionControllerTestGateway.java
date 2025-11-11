package ru.katacademy.bank_app.frauddetection.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для обработки HTTP-запросов
 *
 * Обрабатывает маршруты "/api/fraud".
 * В рамках работы с использованием API Gateway.
 */
@RestController
@RequestMapping("/api/fraud")
public class FraudDetectionControllerTestGateway {

    /**
     * Тестовый endpoint, для проверки маршрутизации через API Gateway.
     *
     * Возвращает HTTP с текстом, что запрос успешно прошёл через gateway
     * и достиг fraud-detection.
     *
     * @return ResponseEntity с телом "Gateway до аккаунта добрался" и HTTP-статусом 200 OK
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Gateway до fraud-detection добрался");
    }
}
