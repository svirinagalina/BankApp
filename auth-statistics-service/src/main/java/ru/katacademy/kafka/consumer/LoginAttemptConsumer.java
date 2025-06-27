package ru.katacademy.kafka.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.katacademy.bank_app.accountservice.domain.events.LoginAttemptedEvent;
import ru.katacademy.domain.entity.LoginAttempt;
import ru.katacademy.domain.repository.LoginAttemptAuthRepository;

/**
 * Сервис-потребитель Kafka для обработки событий о попытках входа в систему.
 * <p>
 * Получает события типа {@link LoginAttemptedEvent} из топика Kafka, преобразует их
 * в сущности {@link LoginAttempt} и сохраняет в базу данных через {@link LoginAttemptAuthRepository}.
 * </p>
 *
 * <p>
 * <b>Конфигурация Kafka:</b>
 * <ul>
 *   <li>Топик: {@code login-attempts-topic}</li>
 *   <li>Группа: {@code auth-statistics-group}</li>
 * </ul>
 *
 * @author MihasBatler
 * @see LoginAttemptedEvent
 */
@Service
@RequiredArgsConstructor
public class LoginAttemptConsumer {
    private final LoginAttemptAuthRepository loginAttemptAuthRepository;

    /**
     * Обрабатывает событие о попытке входа.
     * <p>
     * <b>Алгоритм работы:</b>
     * <ol>
     *   <li>Получает событие из Kafka</li>
     *   <li>Создает новую сущность {@link LoginAttempt}</li>
     *   <li>Заполняет все поля из события</li>
     *   <li>Сохраняет сущность в базу данных</li>
     * </ol>
     *
     * @param event событие попытки входа (не может быть {@code null})
     * @throws IllegalArgumentException если {@code event} равен {@code null}
     */
    @KafkaListener(topics = "login-attempts-topic", groupId = "auth-statistics-group")
    public void consume(LoginAttemptedEvent event) {
        LoginAttempt attempt = new LoginAttempt();
        attempt.setUserId(event.getUserId());
        attempt.setIp(event.getIp());
        attempt.setUserAgent(event.getUserAgent());
        attempt.setTimestamp(event.getTimestamp());
        attempt.setSuccess(event.isSuccess());

        loginAttemptAuthRepository.save(attempt);
    }
}
