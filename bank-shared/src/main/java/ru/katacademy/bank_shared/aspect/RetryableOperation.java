package ru.katacademy.bank_shared.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация-маркер для методов, которые требуют автоматического
 * выполнения повторных попыток при возникновении указанных исключений.
 * <p>
 * Параметры:
 * <ul>
 *   <li><b>maxAttempts</b> — максимальное число попыток (если -1, берётся из конфигурации);</li>
 *   <li><b>backoffDelayMs</b> — задержка между попытками в миллисекундах (если -1, берётся из конфигурации);</li>
 *   <li><b>retryOn</b> — массив типов исключений, при которых выполняется повторный вызов.</li>
 * </ul>
 * </p>
 * <p>
 * Позволяет настроить поведение retry без изменения кода сервиса,
 * используя аннотацию или параметры из <code>application.yml</code>.
 * </p>
 *
 * @author Глеб Ткачёв
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RetryableOperation {
    int maxAttempts() default -1;
    long backoffDelayMs() default -1;
    Class<? extends Throwable>[] retryOn() default {
            java.io.IOException.class,
            java.net.SocketTimeoutException.class
    };
}