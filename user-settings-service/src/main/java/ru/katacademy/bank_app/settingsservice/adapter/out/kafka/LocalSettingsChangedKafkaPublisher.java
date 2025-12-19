package ru.katacademy.bank_app.settingsservice.adapter.out.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.katacademy.bank_app.settingsservice.application.port.out.SettingsChangedEventPublisher;
import ru.katacademy.bank_shared.event.SettingsChangedEvent;

/**
 * Локальная заглушка для публикации событий смены KYC-статуса
 * <p>
 * Этот компонент используется в профиле {@code local} и не взаимодействует с Kafka.
 * Вместо отправки сообщений в топик, событие просто логируется.
 * Позволяет запускать сервис локально, без подключения к внешней инфраструктуре.
 * <p>
 * Использование:
 * - Поддерживает интерфейс {@link SettingsChangedEventPublisher}, чтобы UserRegisterServiceImpl мог вызывать метод publish
 * - Сохраняется порядок вызовов и семантика публикации, но события не уходят в реальную систему
 * - Позволяет тестировать бизнес-логику UserRegisterServiceImpl без ошибок зависимостей
 * <p>
 * Дата: 19-12-2025
 * Автор: Dmitrii Krasitskii
 */
@Component
@Profile("local")
public class LocalSettingsChangedKafkaPublisher implements SettingsChangedEventPublisher {

   private static final Logger logger = LoggerFactory.getLogger(LocalSettingsChangedKafkaPublisher.class);

    @Override
    public void publish(SettingsChangedEvent event) {
        logger.info("local settings event published: {}", event);
    }
}
