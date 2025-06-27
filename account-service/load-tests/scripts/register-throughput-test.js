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
 * - HTML-отчет:        reports/throughput-report.html
 * - JSON-дамп:         reports/throughput-summary.json
 * - Консольный вывод:  Статистика в текстовом виде
 */
import http from 'k6/http';
import { check } from 'k6';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';
import { htmlReport } from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";

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

    return {
        'stdout': textSummary(data, {
            indent: '  ',
            enableColors: true
        }),
        'reports/throughput-summary.json': JSON.stringify(data, null, 2),
        'reports/throughput-report.html': htmlReport(data)
    }
}

