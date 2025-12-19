package ru.katacademy.notification.infrastructure.sender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.katacademy.notification.application.sender.NotificationSender;

/**
 * Локальная заглушка для публикации событий смены KYC-статуса
 * <p>
 * Этот компонент используется в профиле {@code local} и не взаимодействует с Kafka.
 * Вместо отправки сообщений в топик, событие просто логируется.
 * Позволяет запускать сервис локально, без подключения к внешней инфраструктуре.
 * <p>
 * Использование:
 * - Поддерживает интерфейс {@link NotificationSender}, чтобы NotificationServiceImpl мог вызывать метод send
 * - Сохраняется порядок вызовов и семантика публикации, но события не уходят в реальную систему
 * - Позволяет тестировать бизнес-логику NotificationServiceImpl без ошибок зависимостей
 * <p>
 * Дата: 19-12-2025
 * Автор: Dmitrii Krasitskii
 */
@Component
@Profile("local")
public class LocalNotificationSender implements NotificationSender {

    private static final Logger log = LoggerFactory.getLogger(LocalNotificationSender.class);

    @Override
    public void send(String message) {
        message = "Local notification sender has been sent";
        log.info(message);
    }
}