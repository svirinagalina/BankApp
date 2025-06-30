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
 */

import http from 'k6/http';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';

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
    const getMetric = (metric, field, defaultValue = 0) => {
    return data.metrics[metric]?.values?.[field] ?? defaultValue;
};

    const metrics = {
        duration: data.state?.testRunDurationMs ? (data.state.testRunDurationMs / 1000).toFixed(1) : 0,
        count: getMetric('http_reqs', 'count'),
        avg: getMetric('http_req_duration', 'avg'),
        p95: getMetric('http_req_duration', 'p(95)'),
        max: getMetric('http_req_duration', 'max'),
        waiting: getMetric('http_req_waiting', 'avg'),
        errors: getMetric('http_req_failed', 'count'),
        errorRate: getMetric('http_req_failed', 'rate')
    };

    const bottlenecks = [];
    if (metrics.p95 > 500) bottlenecks.push("- **Превышен p95 latency** (SLA: <500мс)");
    if (metrics.max > 1000) bottlenecks.push("- **Критические выбросы задержки** (>1000мс)");
    if (metrics.errors > 0) bottlenecks.push(`- **Ошибки запросов**: ${metrics.errors} (${(metrics.errorRate * 100).toFixed(2)}%)`);
    if (metrics.waiting > metrics.avg * 0.7) bottlenecks.push("- **Высокое время ожидания ответа сервера**");

    const mdReport = `# Отчет LATENCY-теста регистрации пользователей

## Ключевые метрики
| Метрика               | Значение       |
|-----------------------|----------------|
| Среднее время ответа | ${metrics.avg.toFixed(2)} мс |
| 95-й перцентиль (p95) | ${metrics.p95.toFixed(2)} мс |
| Максимальная задержка | ${metrics.max.toFixed(2)} мс |
| Время ожидания (waiting) | ${metrics.waiting.toFixed(2)} мс |
| Успешных запросов    | ${metrics.count - metrics.errors}/${metrics.count} |
| Уровень ошибок       | ${(metrics.errorRate * 100).toFixed(2)}% |
| Всего запросов      | ${metrics.count} |
| Длительность теста  | ${metrics.duration} сек |

## Узкие места
${bottlenecks.length > 0 ? bottlenecks.join('\n') : "- Система работает стабильно, узких мест не обнаружено"}
`;
    return {
        'stdout': textSummary(data, { indent: ' ', enableColors: true }),
        'reports/latency-summary.json': JSON.stringify(data, null, 2),
        'reports/latency-report.md': mdReport
    };
}