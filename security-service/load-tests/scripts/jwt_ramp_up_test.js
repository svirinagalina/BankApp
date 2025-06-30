/**
 * RAMP-UP TEST: Постепенное увеличение нагрузки на JWT-верификацию
 *
 * Цель: Определить, как система масштабируется при увеличении нагрузки.
 * Метрики:
 * - Пропускная способность (RPS)
 * - Задержка (latency) при разной нагрузке
 * - Процент ошибок
 *
 * Тестируемый endpoint:
 * ----------------------------------
 * Метод:               POST
 * URL:                 http://localhost:8085/api/security/verify
 * Content-Type:        application/json
 *
 * Нагрузочный профиль:
 * ----------------------------------
 * Тип теста:           Постепенный рост нагрузки (ramp-up)
 * Этапы:
 * - 0s -> 30s:  Плавный рост от 1 до 50 VU
 * - 30s -> 60s: Рост от 50 до 70 VU
 * - 60s -> 120s: Плавный рост до 100 VU
 * - 120s -> 180s: Пиковая нагрузка 100 VU
 *
 * Критерии успеха:
 * ----------------------------------
 * - 99% запросов с статусом 200
 * - p(95) latency < 300 мс при 100 VU
 * - Ошибки подключения < 1%
 *
 * Конфигурация:
 * ----------------------------------
 * - discardResponseBodies: true (экономия памяти)
 * - noConnectionReuse: true (чистые измерения)
 * - thresholds: SLA по задержке и ошибкам
 */
import http from 'k6/http';
import { check } from 'k6';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';
import * as jsrsasign from 'https://cdn.jsdelivr.net/npm/jsrsasign@8.0.20/lib/jsrsasign.min.js';

export let options = {
    stages: [
        { duration: '30s', target: 50 },
        { duration: '30s', target: 70 },
        { duration: '1m', target: 100 },
        { duration: '1m', target: 100 },
    ],
    discardResponseBodies: true,
    noConnectionReuse: true,
    thresholds: {
        'http_req_duration{type:all}': ['p(95)<300'], // SLA
        'http_req_failed': ['rate<0.01'],
    }
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

export function handleSummary(data) {
    const format = (num, decimals = 2) =>
        typeof num === 'number' ? num.toFixed(decimals) : 'N/A';

    const getStageMetrics = (stageName) => {
        return {
            rps: data.metrics[`http_reqs{scenario:${stageName}}`]?.values?.rate || 0,
            p95: data.metrics[`http_req_duration{scenario:${stageName}}`]?.values?.['p(95)'] || 0,
            errors: data.metrics[`http_req_failed{scenario:${stageName}}`]?.values?.rate || 0
        };
    };

    const stages = {
        '0-50 VU': getStageMetrics('0-50 VU'),
        '50-70 VU': getStageMetrics('50-70 VU'),
        '70-100 VU': getStageMetrics('70-100 VU'),
        '100 VU': getStageMetrics('100 VU')
    };

    // Анализ узких мест
    const bottlenecks = [];
    if (data.metrics.http_req_duration.values['p(95)'] > 300) {
        bottlenecks.push(`- **p95 превышает SLA 300мс** (${format(data.metrics.http_req_duration.values['p(95)'])}мс)`);
    }
    if (data.metrics.http_req_failed.values.rate > 0.01) {
        bottlenecks.push(`- **Уровень ошибок высокий** (${format(data.metrics.http_req_failed.values.rate * 100)}%)`);
    }

    // Генерация Markdown
    const mdReport = `# Отчет по тесту RAMP-UP JWT верификации

- **Всего запросов:** ${data.metrics.http_reqs.values.count}
- **Длительность:** ${(data.state.testRunDurationMs / 1000).toFixed(0)}s

## Результаты по этапам
| Этап       | RPS   | p95 (мс) | Ошибки |
|------------|-------|----------|--------|
| 0-50 VU    | ${format(stages['0-50 VU'].rps)} | ${format(stages['0-50 VU'].p95)} | ${format(stages['0-50 VU'].errors * 100)}% |
| 50-70 VU   | ${format(stages['50-70 VU'].rps)} | ${format(stages['50-70 VU'].p95)} | ${format(stages['50-70 VU'].errors * 100)}% |
| 70-100 VU  | ${format(stages['70-100 VU'].rps)} | ${format(stages['70-100 VU'].p95)} | ${format(stages['70-100 VU'].errors * 100)}% |
| 100 VU     | ${format(stages['100 VU'].rps)} | ${format(stages['100 VU'].p95)} | ${format(stages['100 VU'].errors * 100)}% |

## Итоговые метрики
- **Средний RPS:** ${format(data.metrics.http_reqs.values.rate)}
- **Общий p95:** ${format(data.metrics.http_req_duration.values['p(95)'])} мс
- **Макс. задержка:** ${format(data.metrics.http_req_duration.values.max)} мс
- **Общий уровень ошибок:** ${format(data.metrics.http_req_failed.values.rate * 100)}%

## Узкие места
${bottlenecks.length > 0 ? bottlenecks.join('\n') : '- Система выдерживает нагрузку согласно SLA'}

`;
    return {
        'stdout': textSummary(data, { indent: ' ', enableColors: true }),
        'reports/rampup-summary.json': JSON.stringify(data, null, 2),
        'reports/rampup-report.md': mdReport
    };
}
