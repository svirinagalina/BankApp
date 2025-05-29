package ru.katacademy.bank_app.accountservice.application.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Класс, {@code PasswordChangedEvent} представляющий событие изменения пароля пользователя.
 *
 * <p>Используется для хранения информации о
 * событии, в котором пользователь изменяет свой пароль.</p>
 *
 * <p>Содержит следующие поля:</p>
 * <ul>
 *     <li><b>userId</b> - идентификатор пользователя, чей пароль изменился;</li>
 *     <li><b>timestamp</b> - временная метка, когда произошло изменение;</li>
 *     <li><b>oldPassword</b> - старый пароль пользователя;</li>
 *     <li><b>newPassword</b> - новый пароль пользователя.</li>
 * </ul>
 *
 * <p>Класс поддерживает два конструктора:</p>
 * <ul>
 *     <li>Конструктор по умолчанию, который устанавливает временную
 *         метку на текущее время;</li>
 *     <li>Конструктор, принимающий параметры: идентификатор пользователя,
 *         старый пароль и новый пароль.</li>
 * </ul>
 *
 * <p>Доступ к полям осуществляется с помощью методов-геттеров и
 * методов-сеттеров, которые предоставляются аннотациями
 * <code>@Getter</code> и <code>@Setter</code>.</p>
 *
 * Автор: Колпаков А.С..
 * Дата: 2025-05-07
 */
@Setter
@Getter
public class PasswordChangedEvent {
    private Long userId;
    private LocalDateTime timestamp;
    private String oldPassword;
    private String newPassword;

    public PasswordChangedEvent() {
        this.timestamp = LocalDateTime.now();
    }

    public PasswordChangedEvent(Long userId, String oldPassword, String newPassword) {
        this();
        this.userId = userId;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }
}
