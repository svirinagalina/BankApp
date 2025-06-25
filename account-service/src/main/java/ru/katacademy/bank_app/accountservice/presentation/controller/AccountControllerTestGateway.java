package ru.katacademy.bank_app.accountservice.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для обработки HTTP-запросов, связанных с аккаунтами.
 *
 * Обрабатывает маршруты "/api/accounts".
 * В рамках работы с использованием API Gateway.
 *
 * Автор: Быстров М
 * Дата: 20.06.2025
 */
@RestController
@RequestMapping("/api/accounts")
public class AccountControllerTestGateway {

    /**
     * Тестовый endpoint, для проверки маршрутизации через API Gateway.
     *
     * Возвращает HTTP с текстом, что запрос успешно прошёл через gateway
     * и достиг account-service.
     *
     * @return ResponseEntity с телом "Gateway до аккаунта добрался" и HTTP-статусом 200 OK
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Gateway до аккаунта добрался");
    }
}
