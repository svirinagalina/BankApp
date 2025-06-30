/**
 * THROUGHPUT TEST: Определение максимальной пропускной способности JWT-верификации
 *
 * Цель: Определить устойчивую пропускную способность системы при сохранении SLA
 *
 * Методика:
 * - Тест с постоянной интенсивностью (constant-arrival-rate)
 * - Начальная нагрузка: 200 запросов/секунду
 * - Продолжительность: 2 минуты
 * - Виртуальные пользователи: 100-1000 (автомасштабирование)
 *
 * Критерии качества:
 * - p95 времени ответа < 500 мс
 * - Уровень ошибок < 1%
 *
 * Ключевые метрики:
 * - RPS (фактический/целевой)
 * - Время ответа (avg, p95, max)
 * - Количество ошибок
 * - Использование VUs
 *
 * Анализ узких мест:
 * - Высокий p95 (>50мс)
 * - Выбросы задержки (>300мс)
 * - Ошибки верификации
 */
import http from 'k6/http';
import { check } from 'k6';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';
import * as jsrsasign from 'https://cdn.jsdelivr.net/npm/jsrsasign@8.0.20/lib/jsrsasign.min.js';

export const options = {
    discardResponseBodies: false,
    scenarios: {
        throughput: {
            executor: 'constant-arrival-rate',
            rate: 200,
            timeUnit: '1s',
            duration: '2m',
            preAllocatedVUs: 100,
            maxVUs: 1000
        }
    },
    thresholds: {
        http_req_duration: ['p(95)<500'],
        http_req_failed: ['rate<0.01']
    },
    summaryTrendStats: ['avg', 'min', 'med', 'max', 'p(90)', 'p(95)', 'count']
};

function generateToken(userId) {
    const header = { alg: 'HS256', typ: 'JWT' };
    const payload = {
        sub: userId,
        iat: Math.floor(Date.now() / 1000),
        exp: Math.floor(Date.now() / 1000) + 3600
    };
    const secret = "testsecretkeyfortestpurposesonly1234567890";
    return jsrsasign.KJUR.jws.JWS.sign("HS256", JSON.stringify(header), JSON.stringify(payload), secret);
}

const token = Array(10).fill().map((_, i) => generateToken(`user${i}`));

export default function () {
    const currentToken = token[__VU % token.length];
    const res = http.post('http://localhost:8085/api/security/verify',
        currentToken,
        {headers: {'Content-Type': 'application/json'}}
    );

    check(res, {
        'is status 200': (r) => r.status === 200,
    });
}

function detectBottlenecks(metrics) {
    const bottlenecks = [];
    const p95 = metrics.http_req_duration.values.p95;
    const max = metrics.http_req_duration.values.max;

    if (p95 > 50) {
        bottlenecks.push(`Высокий p95 (${p95.toFixed(2)}мс): 5% запросов обрабатываются дольше 50мс`);
    }

    if (max > 300) {
        bottlenecks.push(`Выбросы задержки (до ${max.toFixed(2)}мс): Есть отдельные медленные запросы`);
    }

    if (metrics.http_req_failed.values.count > 0) {
        bottlenecks.push(`Ошибки (${metrics.http_req_failed.values.count}): Проверьте логи сервера`);
    }

    return bottlenecks.length > 0 ? bottlenecks : ["Явных узких мест не обнаружено"];
}

export function handleSummary(data) {
    const getSafe = (obj, prop, def = 0) => obj && obj[prop] !== undefined ? obj[prop] : def;

    const httpReqs = getSafe(data.metrics.http_reqs, 'values', {count: 0, rate: 0});
    const httpDuration = getSafe(data.metrics.http_req_duration, 'values', {avg: 0, p95: 0, max: 0});
    const httpFailed = getSafe(data.metrics.http_req_failed, 'values', {count: 0, rate: 0});

    const durationSec = data.state ? (data.state.testRunDurationMs / 1000).toFixed(0) : '0';
    const targetRate = 200;

    const fmt = (num) => num !== undefined ? num.toFixed(2) : 'N/A';
    const fmtPerc = (num) => num !== undefined ? (num * 100).toFixed(2) : 'N/A';
    const bottlenecks = detectBottlenecks(data.metrics);
    const getP95 = () => {
        return data.metrics['http_req_duration{expected_response:true}']?.values?.['p(95)']
            || data.metrics.http_req_duration?.values?.['p(95)']
            || data.metrics.http_req_duration?.values?.p95;
    };
    const metrics = {
        p95: getP95() || 0
    };

    const mdReport = `# Отчёт по нагрузочному тестированию JWT


## Основные метрики
| Параметр               | Значение          |
|------------------------|-------------------|
| Длительность теста     | ${durationSec} сек|
| Всего запросов         | ${httpReqs.count} |
| RPS (факт/цель)        | ${fmt(httpReqs.rate)}/${targetRate} |
| Среднее время ответа   | ${fmt(httpDuration.avg)} мс |
| p95 время ответа       | ${fmt(metrics.p95)} мс |
| Максимальная задержка  | ${fmt(httpDuration.max)} мс |
| Ошибки                 | ${httpFailed.count} (${fmtPerc(httpFailed.rate)}%) |

## Узкие места
${bottlenecks.map(b => `- ${b}`).join('\n')}
`;

    return {
        'stdout': textSummary(data, { indent: ' ', enableColors: true }),
        'reports/jwt-throughput-summary.json': JSON.stringify(data, null, 2),
        'reports/jwt-throughput-report-ru.md': mdReport
    };
}
