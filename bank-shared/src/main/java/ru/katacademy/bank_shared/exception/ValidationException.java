package ru.katacademy.bank_shared.exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Исключение, которое используется для обработки ошибок валидации в приложении.
 * Это исключение может быть выброшено, когда данные, переданные пользователем, не проходят валидацию.
 * Например, это может быть вызвано нарушением ограничений, таких как некорректный формат email,
 * неверная длина пароля и т.п.
 */

public class ValidationException extends RuntimeException {
    private List<String> errors;

    /**
     * Конструктор класса ValidationException.
     * Конструктор создает экземпляр исключения, используя переданный список ошибок.
     * Список ошибок сохраняется в виде его копии.
     *
     * @param errors Список сообщений об ошибках валидации.
     */
    public ValidationException(List<String> errors) {
        this.errors = new ArrayList<>(errors);
    }

    /**
     * Получает список ошибок валидации.
     * Этот метод возвращает неизменяемый список ошибок.
     *
     * @return Неизменяемый список строк, содержащих сообщения об ошибках валидации.
     */
    public List<String> getErrors() {
        return Collections.unmodifiableList(errors); // Возвращаем неизменяемый список
    }
}
