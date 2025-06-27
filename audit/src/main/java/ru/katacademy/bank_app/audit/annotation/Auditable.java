package ru.katacademy.bank_app.audit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для маркировки бизнес-методов, вызовы которых должны
 * автоматически фиксироваться в системе аудита.
 * <p>
 * Используется в сочетании с аспектом аудита (AuditAspect) для
 * перехвата вызовов методов и формирования соответствующих событий аудита.
 * <p>
 * Атрибут:
 * <ul>
 *   <li>{@code action} — строковое описание действия (например, "Создание аккаунта", "Смена пароля"),
 *   которое будет записано в событие аудита.</li>
 * </ul>
 * <p>
 * Аннотация должна применяться к методам бизнес-логики, которые необходимо отслеживать.
 *
 * @author Ваше_имя
 * @since 2025-06-27
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Auditable {
    /**
     * Описание действия, которое будет зафиксировано в аудите.
     *
     * @return описание действия
     */
    String action();
}

