/**
 * THROUGHPUT TEST: Измерение пропускной способности API регистрации пользователей
 *
 * Цель: Определить максимальное количество успешных регистраций в секунду (RPS)
 *       при соблюдении SLA по времени ответа
 *
 * Метрика: Количество успешных POST-запросов/сек с кодом 201
 *
 * Параметры теста:
 * ----------------------------------
 * Тип теста:            Постоянная частота запросов (constant-arrival-rate)
 * Endpoint:             POST /api/users/register
 * Content-Type:         application/json
 *
 * Нагрузочный профиль:
 * - Стартовая частота:  5 RPS
 * - Целевая частота:    10 RPS (автомасштабирование)
 * - Минимальные VUs:    5
 * - Максимальные VUs:   10
 * - Продолжительность:  1 минута
 * - Таймаут запроса:    15 секунд
 *
 * Тестовые данные:
 * - Уникальные email:   loadtest.{VU_ID}.{ITER}@example.com
 * - Динамические пароли: P@ss-{VU_ID}-{TIMESTAMP}
 *
 * Критерии успеха:
 * - Пропускная способность ≥ 5 RPS (abortOnFail)
 * - 95-й перцентиль задержки < 1000 мс
 * - < 5% ошибок (HTTP-код ≠ 201 или таймаут)
 *
 * Собираемые метрики:
 * - http_reqs:          Общее количество запросов
 * - http_req_rate:      Фактический RPS
 * - http_req_duration:  Время выполнения запросов (p95, p99)
 * - http_req_failed:    Процент неудачных запросов
 * - vus:                Используемые виртуальные пользователи
 *
 * Генерация отчетов:
 * - JSON-дамп:         reports/throughput-summary.json
 * - Консольный вывод:  Статистика в текстовом виде
 */
import http from 'k6/http';
import { check } from 'k6';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';

export const options = {
    scenarios: {
        throughput_test: {
            executor: 'constant-arrival-rate',
            rate: 5,
            timeUnit: '1s',
            duration: '1m',
            preAllocatedVUs: 5,
            maxVUs: 10
        }
    },
    thresholds: {
        'http_reqs{scenario:throughput_test}': [
            { threshold: 'rate >= 5', abortOnFail: false }
        ],
        'http_req_duration{status:201}': [
            { threshold: 'p(95) < 1000' }
        ],
        'http_req_failed': [
            { threshold: 'rate < 0.05', abortOnFail: false }
        ]
    },
    summaryTrendStats: [
        'avg', 'min', 'med', 'max', 'p(95)', 'p(99)', 'count'
    ]
};

function generateTestData(vuId) {
    const timestamp = Date.now();
    return {
        fullName: `User ${vuId}-${timestamp}`,
        email: `test.${vuId}.${timestamp}@test.com`,
        password: `ValidPass123!${vuId}`
    };
}

export default function () {
    const data = generateTestData(__VU);
    const res = http.post(
        'http://localhost:8084/api/users/register',
        JSON.stringify(data),
        {
            headers: { 'Content-Type': 'application/json',
                'X-Debug': 'true'},
            timeout: '15s',
            retries: 1
        }
    );
    if (res.status !== 201) {
        console.error(`ERROR ${res.status}: ${res.body}`);
        console.error(`Request data: ${JSON.stringify(data)}`);
    }

    check(res, {
        'status is 201': (r) => r.status === 201,
        'response time acceptable': (r) => r.timings.duration < 1000
    });
}

export function handleSummary(data) {
    const targetRPS = 100;
    const actualRPS = data.metrics.http_reqs.rate;
    const rpsPercentage = (actualRPS / targetRPS * 100).toFixed(1);
    const getSafe = (obj, prop, def = 0) => obj && obj[prop] !== undefined ? obj[prop] : def;

    const httpReqs = getSafe(data.metrics.http_reqs, 'values', {count: 0, rate: 0});
    const httpDuration = getSafe(data.metrics.http_req_duration, 'values', {avg: 0, p95: 0, max: 0});
    const httpFailed = getSafe(data.metrics.http_req_failed, 'values', {count: 0, rate: 0});
    const vus = getSafe(data.metrics.vus, 'values', {max: 0});

    const durationSec = data.state ? (data.state.testRunDurationMs / 1000).toFixed(0) : '0';
    const targetRate = 5;

    const fmt = (num) => num !== undefined ? num.toFixed(2) : 'N/A';
    const fmtPerc = (num) => num !== undefined ? (num * 100).toFixed(2) + '%' : 'N/A';

    const getP95 = () => {
        return data.metrics['http_req_duration{status:201}']?.values?.['p(95)']
            || data.metrics.http_req_duration?.values?.['p(95)']
            || 0;
    };

    const bottlenecks = [];
    if (httpReqs.rate < targetRate) {
        bottlenecks.push(`Низкий RPS (${fmt(httpReqs.rate)} при целевом ${targetRate})`);
    }
    if (getP95() > 1000) {
        bottlenecks.push(`Превышен p95 latency (${fmt(getP95())} мс при SLA 1000мс)`);
    }
    if (httpFailed.rate > 0.05) {
        bottlenecks.push(`Высокий уровень ошибок (${fmtPerc(httpFailed.rate)} при допустимом 5%)`);
    }
    if (vus.max >= 10) {
        bottlenecks.push(`Достигнут максимум виртуальных пользователей (${vus.max} VUs)`);
    }

    const mdReport = `# Отчёт по тесту пропускной способности API регистрации

## Ключевые метрики
| Метрика               | Значение          |
|------------------------|-------------------|
| Фактический RPS       | ${fmt(httpReqs.rate)} |
| Успешных запросов     | ${httpReqs.count} (${fmtPerc(1 - httpFailed.rate)}) |
| Среднее время ответа  | ${fmt(httpDuration.avg)} мс |
| p95 время ответа      | ${fmt(getP95())} мс |
| Максимальная задержка | ${fmt(httpDuration.max)} мс |
| Уровень ошибок        | ${fmtPerc(httpFailed.rate)} |
| Использовано VUs      | ${vus.max} |
| Целевой RPS           | ${targetRate} запр/сек |
| Длительность          | ${durationSec} сек|

## Узкие места
${bottlenecks.length > 0
        ? bottlenecks.map(b => `-  ${b}`).join('\n')
        : '- Все показатели соответствуют SLA'}
`;


    return {
        'stdout': textSummary(data, {
            indent: '  ',
            enableColors: true
        }),
        'reports/throughput-summary.json': JSON.stringify(data, null, 2),
        'reports/throughput-report.md': mdReport
    }
}

