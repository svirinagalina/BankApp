/**
 * SOAK TEST: Длительная нагрузка API регистрации пользователей для выявления узких мест
 *
 * Цель: Проверить стабильность системы при продолжительной нагрузке, выявить:
 *       - Утечки памяти
 *       - Деградацию производительности
 *       - Проблемы с подключениями к БД
 *
 * Метрика: Стабильность RPS и времени ответа в течение 30 минут
 *
 * Параметры теста:
 * ----------------------------------
 * Тип теста:            Постоянная частота запросов (constant-arrival-rate)
 * Endpoint:             POST /api/users/register
 * Content-Type:         application/json
 *
 * Нагрузочный профиль:
 * - Частота запросов:   5 RPS (фиксированная)
 * - Минимальные VUs:    5
 * - Максимальные VUs:   10 (автомасштабирование при необходимости)
 * - Продолжительность:  30 минут
 * - Таймаут запроса:    10 секунд
 *
 * Тестовые данные:
 * - Уникальные имена:   Soak User {VU_ID}-{ITERATION}
 * - Уникальные email:   soak.{VU_ID}.{ITERATION}@test.com
 * - Динамические пароли: SoakPass-{TIMESTAMP}
 *
 * Критерии успеха:
 * - 95-й перцентиль задержки < 500 мс (abortOnFail)
 * - < 1% ошибок (HTTP-код ≠ 201 или таймаут)
 *
 * Особенности конфигурации:
 * - noConnectionReuse:     true (имитация поведения реальных пользователей)
 * - discardResponseBodies: true (экономия памяти при длительном тесте)
 *
 * Собираемые метрики:
 * - http_reqs:          Общее количество запросов
 * - http_req_rate:      Фактический RPS
 * - http_req_duration:  Время выполнения запросов (p95, p99)
 * - http_req_failed:    Процент неудачных запросов
 * - vus:                Используемые виртуальные пользователи
 *
 * Генерация отчетов:
 * - HTML-отчет:        reports/soak-report.html
 * - JSON-дамп:         reports/soak-summary.json
 * - Консольный вывод:  Статистика в текстовом виде с цветовой разметкой
 */

import http from 'k6/http';
import { check, sleep } from 'k6';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';
import { htmlReport } from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";

export const options = {
    scenarios: {
        soak_test: {
            executor: 'constant-arrival-rate',
            rate: 5,
            timeUnit: '1s',
            duration: '30m',
            preAllocatedVUs: 5,
            maxVUs: 10
        }
    },
    thresholds: {
        http_req_duration: ['p(95) < 500'],
        http_req_failed: ['rate < 0.01']
    },
    noConnectionReuse: true,
    discardResponseBodies: true
};

function generateTestData(vuId) {
    return {
        fullName: `Soak User ${vuId}-${__ITER}`,
        email: `soak.${vuId}.${__ITER}@test.com`,
        password: `SoakPass-${Date.now()}`
    };
}

export default function () {
    const data = generateTestData(__VU);
    const res = http.post(
        'http://localhost:8084/api/users/register',
        JSON.stringify(data),
        {
            headers: { 'Content-Type': 'application/json' },
            timeout: '10s'
        }
    );

    check(res, {
        'status is 201': (r) => r.status === 201
    });
}

export function handleSummary(data) {
    return {
        'stdout': textSummary(data, { indent: ' ', enableColors: true }),
        'reports/soak-summary.json': JSON.stringify(data, null, 2),
        'reports/soak-report.html': htmlReport(data)
    };
}