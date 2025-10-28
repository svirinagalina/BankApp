package ru.katacademy.bank_app.frauddetection.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

// Feign клиент к user-service для вызова аккаунта
@FeignClient(name = "account-service", url = "${user.service.url}")
public interface AccountClient {

    // При вызове этого метода Feign выполнит HTTP GET на {user.service.url}/api/accounts/{id}
    @GetMapping("/api/accounts/{id}")
    ResponseEntity<AccountDto> getById(@PathVariable("id") Long id);

    @PostMapping("/api/accounts/{id}/block")
    void blockById(@PathVariable("id") Long id);
}
