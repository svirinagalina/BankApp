package ru.katacademy.apigateway.health;

/**
 * Состояние сервиса для определения api-gateway дальнейших действий по маршрутизации трафика
 *
 * <ul>
 *   <li>READY — сервис полностью готов - gateway может направлять на него пользовательский трафик</li>
 *   <li>NOT_READY — сервис жив, но не готов, gateway временно не должен слать на него запросы</li>
 *   <li>UNAVAILABLE — сервис недоступен, gateway исключает его из маршрутизации</li>
 * </ul>
 *
 * Автор: Krasitskii Dmitrii
 * дата: 29.12.2025
 */
public enum ServiceStatus {
    READY,
    NOT_READY,
    UNAVAILABLE
}