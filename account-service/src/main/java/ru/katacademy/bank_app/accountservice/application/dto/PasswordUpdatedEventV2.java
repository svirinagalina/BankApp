package ru.katacademy.bank_app.accountservice.application.dto;

import java.time.LocalDateTime;

/**
 * Класс, {@code PasswordUpdatedEventV2} представляющий событие обновление пароля пользователя.
 *
 * <p>Используется для хранения информации о
 * событии, в котором пользователь изменяет свой пароль.</p>
 *
 * <p>Содержит следующие поля:</p>
 * <ul>
 *     <li><b>userId</b> - идентификатор пользователя, чей пароль изменился;</li>
 *     <li><b>timestamp</b> - временная метка, когда произошло изменение;</li>
 * </ul>
 *
 * <p>Класс поддерживает два конструктора:</p>
 * <ul>
 *     <li>Конструктор по умолчанию, который устанавливает временную
 *         метку на текущее время;</li>
 *     <li>Конструктор, принимающий параметры: идентификатор пользователя.</li>
 * </ul>
 *
 * <p>Доступ к полям осуществляется с помощью методов-геттеров и
 * методов-сеттеров, которые предоставляются аннотациями
 * <code>@Getter</code> и <code>@Setter</code>.</p>
 *
 * Автор: Колпаков А.С..
 * Дата: 2025-05-07
 */


public class PasswordUpdatedEventV2 {
    private final Long userId;

    public PasswordUpdatedEventV2(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
}
