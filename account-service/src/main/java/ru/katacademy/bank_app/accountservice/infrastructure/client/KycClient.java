package ru.katacademy.bank_app.accountservice.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.katacademy.bank_app.accountservice.application.dto.KycRequestDTO;


@FeignClient(
        name = "kyc-service",
        url = "${kyc-service.url}",
        fallback = KycClientFallback.class
)
public interface KycClient {

    @GetMapping("/kyc/{userId}")
    KycRequestDTO getKyc(@PathVariable("userId") Long userId);
}
