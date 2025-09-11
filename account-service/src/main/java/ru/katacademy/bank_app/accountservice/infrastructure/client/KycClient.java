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

    /**
     * Feign клиент для взаимодействия с KYC-сервисом.
     * <p>
     * Предоставляет интерфейс для получения информации о статусе KYC (Know Your Customer) верификации пользователей.
     * Интеграция с KYC-сервисом осуществляется через REST API с использованием Spring Cloud OpenFeign.
     * </p>
     *
     * <p>
     * Клиент настроен с использованием механизма fallback для обеспечения отказоустойчивости.
     * В случае недоступности KYC-сервиса автоматически используется резервная
     * реализация {@link KycClientFallback}
     *<p>
     * <b>Механизм fallback:</b> При возникновении ошибок соединения, таймаутов или серверных ошибок (5xx),
     * Feign автоматически переключается на реализацию {@link KycClientFallback}, которая возвращает
     * предопределенные значения или выполняет альтернативную логику обработки.
     * </p>
     *
     *  * @see KycClientFallback
     *  * @see KycRequestDTO
     *  * @see org.springframework.cloud.openfeign.FeignClient
     * <p>
     *  Автор: Урбагаев Е.Д.
     *  Дата: 2025-09-09
     */
    @GetMapping("/kyc/{userId}")
    KycRequestDTO getKyc(@PathVariable("userId") Long userId);
}
