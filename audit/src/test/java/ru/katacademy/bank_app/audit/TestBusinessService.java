package ru.katacademy.bank_app.audit;

import org.springframework.stereotype.Service;
import ru.katacademy.bank_app.audit.annotation.Auditable;

/**
 * Тестовый сервис для демонстрации работы аспекта аудита.
 * <p>
 * Содержит методы, помеченные аннотацией {@link Auditable}, которые моделируют
 * успешное и неуспешное выполнение бизнес-операций.
 * <p>
 * Аспект автоматически отправляет события аудита после успешного выполнения методов,
 * и пропускает аудит в случае исключения.
 */
@Service
public class TestBusinessService {

    /**
     * Успешная операция.
     * <p>
     * Возвращает строку с результатом, вызывается с аннотацией {@code @Auditable}
     * с действием "Успешная операция", что позволяет зафиксировать событие аудита.
     *
     * @param input входной параметр операции
     * @return результат операции
     */
    @Auditable(action = "Успешная операция")
    public String successOperation(String input) {
        return "Success: " + input;
    }

    /**
     * Операция, завершающаяся ошибкой.
     * <p>
     * Выбрасывает исключение {@link RuntimeException}, что приводит к пропуску
     * отправки события аудита.
     */
    @Auditable(action = "Ошибка операции")
    public void failOperation() {
        throw new RuntimeException("Failure!");
    }
}

