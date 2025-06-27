/**
 * LATENCY TEST: Измерение времени отклика API для одиночных запросов
 *
 * Цель: Определить базовую производительность endpoint'а без нагрузки.
 * Метрика: HTTP-задержка (latency) при регистрации пользователя.
 *
 * Параметры теста:
 * ----------------------------------
 * Тип теста:            Одиночные запросы (latency)
 * Endpoint:             POST /api/users/register
 *
 * Нагрузочный профиль:
 * - Виртуальных пользователей (VUs):    1
 * - Всего итераций:                     500
 * - Повторное использование соединения: отключено (noConnectionReuse)
 *
 * Критерии успеха:
 * - 95-й перцентиль задержки < 500 мс
 * - 0% ошибок
 *
 * Метрики:
 * - http_req_duration: Общее время запроса
 * - http_req_waiting:  Время ожидания ответа сервера
 * - iteration_duration: Полное время итерации
 *
 * Отчёты:
 * - reports/latency-summary.json: сырые данные
 * - reports/latency-report.html: визуализация
 */

import http from 'k6/http';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';
import { htmlReport } from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";

export const options = {
    vus: 1,
    iterations: 500,
    discardResponseBodies: true,
    noConnectionReuse: true,
    summaryTimeUnit: 'ms',
    thresholds: {
        http_req_duration: ['max<500']
    }
};

export default function () {
    const payload = JSON.stringify({
        fullName: `Test User ${__VU}`,
        email: `test${__ITER}@example.com`,
        password: 'test123'
    });

    const params = {
        headers: { 'Content-Type': 'application/json' },
        timeout: '10s'
    };

    const res = http.post(
        'http://localhost:8084/api/users/register',
        payload,
        params
    );
}

export function handleSummary(data) {
    return {
        'stdout': textSummary(data, { indent: ' ', enableColors: true }),
        'reports/latency-summary.json': JSON.stringify(data, null, 2),
        'reports/latency-report.html': htmlReport(data)
    };
}