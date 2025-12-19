package ru.katacademy.kycservice.infrastructure.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.katacademy.bank_shared.event.kyc.KycStatusChangedEvent;
import ru.katacademy.kycservice.application.port.out.KycEventPublisher;

/**
 * Локальная заглушка для публикации событий смены KYC-статуса
 * <p>
 * Этот компонент используется в профиле {@code local} и не взаимодействует с Kafka.
 * Вместо отправки сообщений в топик, событие просто логируется.
 * Позволяет запускать сервис локально, без подключения к внешней инфраструктуре.
 * <p>
 * Использование:
 * - Поддерживает интерфейс {@link KycEventPublisher}, чтобы KycService мог вызывать метод publish
 * - Сохраняется порядок вызовов и семантика публикации, но события не уходят в реальную систему
 * - Позволяет тестировать бизнес-логику KycService без ошибок зависимостей
 * <p>
 * Дата: 19-12-2025
 * Автор: Dmitrii Krasitskii
 */
@Component
@Profile("local")
public class LocalKycEventPublisher implements KycEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(LocalKycEventPublisher.class);

    @Override
    public void publish(KycStatusChangedEvent event) {
        log.info("LOCAL MODE: Kyc event ignored: {}", event);
    }
}