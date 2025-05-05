package ru.katacademy.bank_shared.event;

import java.time.LocalDateTime;

/**
 * 📢 Событие, публикуемое при успешной регистрации нового пользователя.
 * <p>
 * Используется для уведомлений, аналитики и других действий, связанных с созданием пользователя.
 * Отправляется в Kafka-топик {@code user-register-event}.
 *
 * <p><b>Пример использования:</b></p>
 * <pre>{@code
 * UserRegisterEvent event = new UserRegisterEvent(1L, "Иван Иванов", "ivan@example.com", LocalDateTime.now());
 * producer.send("user-register-event", event.toString());
 * }</pre>
 *
 * @param userId    Идентификатор пользователя
 * @param fullName  Полное имя пользователя
 * @param email     Адрес электронной почты
 * @param createdAt Время регистрации пользователя
 * @author Sheffy
 */
public record UserRegisterEvent(
        long userId,
        String fullName,
        String email,
        LocalDateTime createdAt
) {
}
