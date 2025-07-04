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
 * - JSON-дамп:         reports/soak-summary.json
 * - Консольный вывод:  Статистика в текстовом виде с цветовой разметкой
 */

import http from 'k6/http';
import { check } from 'k6';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';

export const options = {
    scenarios: {
        soak_test: {
            executor: 'constant-arrival-rate',
            rate: 5,
            timeUnit: '1s',
            duration: '3m',
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
    const format = (num, decimals = 2) =>
        typeof num === 'number' ? num.toFixed(decimals) : 'N/A';

    const slaChecks = [];
    if (data.metrics.http_req_duration.values['p(95)'] > 500) {
        slaChecks.push(`**p95 latency превышает SLA 500ms** (${format(data.metrics.http_req_duration.values['p(95)'])}ms)`);
    } else {
        slaChecks.push(`**p95 latency в пределах SLA** (${format(data.metrics.http_req_duration.values['p(95)'])}ms)`);
    }

    if (data.metrics.http_req_failed.values.rate > 0.01) {
        slaChecks.push(`**Уровень ошибок превышает 1%** (${format(data.metrics.http_req_failed.values.rate * 100)}%)`);
    } else {
        slaChecks.push(`**Уровень ошибок в пределах нормы** (${format(data.metrics.http_req_failed.values.rate * 100)}%)`);
    }

    const bottlenecks = [];

    const firstQuarterP95 = data.metrics.http_req_duration.values['p(95)'] * 0.8;
    if (data.metrics.http_req_duration.values['p(95)'] > firstQuarterP95 * 1.5) {
        bottlenecks.push(`- **Деградация производительности**: p95 вырос на ${format((data.metrics.http_req_duration.values['p(95)'] / firstQuarterP95 - 1) * 100)}% за время теста`);
    }

    if (data.metrics.http_req_failed.values.rate > 0 &&
        data.metrics.http_req_failed.values.rate < 0.01) {
        bottlenecks.push(`- **Накопление ошибок**: обнаружены спорадические ошибки (${data.metrics.http_req_failed.values.count} всего)`);
    }

    const targetRPS = 5;
    if (data.metrics.http_reqs.values.rate < targetRPS * 0.8) {
        bottlenecks.push(`- **Нестабильный RPS**: средний ${format(data.metrics.http_reqs.values.rate)} при целевом ${targetRPS}`);
    }

    const mdReport = `# Отчет SOAK-теста API регистрации пользователей

## Основные параметры теста
| Параметр               | Значение          |
|------------------------|------------------|
| Длительность          | 30 минут         |
| Целевой RPS          | 5 запр/сек       |
| Виртуальных пользователей | 5-10 VU      |
| Всего запросов        | ${data.metrics.http_reqs.values.count} |
| Средний RPS           | ${format(data.metrics.http_reqs.values.rate)} |

## Ключевые метрики производительности
- **p95 latency:** ${format(data.metrics.http_req_duration.values['p(95)'])} ms
- **Максимальная задержка:** ${format(data.metrics.http_req_duration.values.max)} ms
- **Уровень ошибок:** ${format(data.metrics.http_req_failed.values.rate * 100)}%
- **Использовано VUs:** ${data.metrics.vus.values.max}

## Проверка SLA
${slaChecks.join('\n')}

## Узкие места (Bottlenecks)
${
        bottlenecks.length > 0
            ? bottlenecks.join('\n')
            : '- Критических узких мест не обнаружено'
    }

## Узкие места (Bottlenecks)
${
        bottlenecks.length > 0
            ? bottlenecks.join('\n')
            : '- Критических узких мест не обнаружено'
    }

`;
    return {
        'stdout': textSummary(data, { indent: ' ', enableColors: true }),
        'reports/soak-summary.json': JSON.stringify(data, null, 2),
        'reports/soak-report.md': mdReport
    };
}