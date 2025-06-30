/**
 * SOAK TEST: Длительная нагрузка на JWT-верификацию
 *
 * Цель: Выявить проблемы, проявляющиеся со временем:
 * - Утечки памяти
 * - Деградация производительности
 * - Накопление ошибок
 *
 * Параметры:
 * ----------------------------------
 * Длительность:          30 минут
 * Виртуальных пользователей: 50 (оптимальная нагрузка из предыдущих тестов)
 * Ожидаемый RPS:         ~120-150 запросов/сек
 *
 * Критерии успеха:
 * ----------------------------------
 * - Память сервера стабильна (±5% за час)
 * - p(95) latency < 400ms на протяжении теста
 * - Ошибки < 0.1%
 */
import http from 'k6/http';
import { check } from 'k6';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';
import * as jsrsasign from 'https://cdn.jsdelivr.net/npm/jsrsasign@8.0.20/lib/jsrsasign.min.js';

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

const tokens = Array(10).fill().map((_, i) => generateToken(`user${i}`));

export let options = {
    vus: 50,
    duration: '30m',
    discardResponseBodies: true,
    noConnectionReuse: false,
    thresholds: {
        'http_req_duration{type:all}': ['p(95)<400'],
        'http_req_failed': ['rate<0.001'],
    }
};

export default function () {
    const token = tokens[__VU % tokens.length];

    const response = http.post(
        'http://localhost:8085/api/security/verify',
        token,
        {
            headers: { 'Content-Type': 'application/json' },
            timeout: '10s'
        }
    );

    check(response, {
        'Status is 200': (r) => r.status === 200,
        'Response time OK': (r) => r.timings.duration < 500
    });
}

export function handleSummary(data) {
    const format = (num, decimals = 2) =>
        typeof num === 'number' ? num.toFixed(decimals) : 'N/A';

    const slaChecks = [];
    if (data.metrics.http_req_duration.values['p(95)'] > 400) {
        slaChecks.push(` **p95 latency превышает SLA 400ms** (${format(data.metrics.http_req_duration.values['p(95)'])}ms)`);
    } else {
        slaChecks.push(`**p95 latency в пределах SLA** (${format(data.metrics.http_req_duration.values['p(95)'])}ms)`);
    }

    if (data.metrics.http_req_failed.values.rate > 0.001) {
        slaChecks.push(`**Уровень ошибок превышает 0.1%** (${format(data.metrics.http_req_failed.values.rate * 100)}%)`);
    } else {
        slaChecks.push(`**Уровень ошибок в пределах нормы** (${format(data.metrics.http_req_failed.values.rate * 100)}%)`);
    }
    const failedChecks = data.metrics.checks ? data.metrics.checks.fails : 0;
    const totalChecks = data.metrics.checks ? data.metrics.checks.passes + failedChecks : 0;
    const failedChecksRate = totalChecks > 0 ? failedChecks / totalChecks : 0;

    const bottlenecks = [];

    const lastThirdDuration = data.metrics.http_req_duration.values['p(95)'] * 1.2;
    if (data.metrics.http_req_duration.values['p(95)'] > lastThirdDuration) {
        bottlenecks.push(`- **Деградация производительности**: p95 вырос на ${format((data.metrics.http_req_duration.values['p(95)'] / lastThirdDuration - 1) * 100)}% за время теста`);
    }

    if (data.metrics.http_req_failed.values.rate > 0 &&
        data.metrics.http_req_failed.values.rate < 0.001) {
        bottlenecks.push(`- **Накопление ошибок**: обнаружены спорадические ошибки (${data.metrics.http_req_failed.values.count} всего)`);
    }

    const targetRPS = 120;
    if (data.metrics.http_reqs.values.rate < targetRPS * 0.9) {
        bottlenecks.push(`- **Низкая пропускная способность**: RPS ${format(data.metrics.http_reqs.values.rate)} при целевом ${targetRPS}`);
    }

    const mdReport = `# Отчет SOAK-теста JWT верификации

## Основные параметры теста
| Параметр               | Значение          |
|------------------------|------------------|
| Длительность          | 30 минут         |
| Виртуальных пользователей | 50 VU         |
| Всего запросов        | ${data.metrics.http_reqs.values.count} |
| Средний RPS           | ${format(data.metrics.http_reqs.values.rate)} |
| Проверок (checks)     | ${totalChecks} (${failedChecks} failed) |

## Ключевые метрики производительности
- **p95 latency:** ${format(data.metrics.http_req_duration.values['p(95)'])} ms
- **Максимальная задержка:** ${format(data.metrics.http_req_duration.values.max)} ms
- **Уровень ошибок:** ${format(data.metrics.http_req_failed.values.rate * 100)}%
- **Пропускная способность:** ${format(data.metrics.http_reqs.values.rate)} запр/сек
- **Ошибки проверок:** ${format(failedChecksRate * 100)}%

## Проверка SLA
${slaChecks.join('\n')}

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
