/**
 * RAMP-UP TEST: Тестирование масштабируемости API регистрации пользователей
 *
 * Цель: Определить максимальную нагрузку, которую система может выдержать
 *       до нарушения SLA по времени ответа и количеству ошибок
 *
 * Метрика: Максимальное количество виртуальных пользователей (VUs)
 *          при соблюдении критериев производительности
 *
 * Параметры теста:
 * ----------------------------------
 * Тип теста:            Прогрессивный рост нагрузки (ramping-vus)
 * Endpoint:             POST /api/users/register
 * Content-Type:         application/json
 *
 * Нагрузочный профиль:
 * - Стартовые VUs:      0
 * - Этапы роста:
 *   - 0 → 10 VU за 2 минуты
 *   - 10 → 20 VU за 1 минуту
 *   - 20 → 30 VU за 30 секунд
 * - Плавное завершение: 30 секунд
 * - Таймаут запроса:    15 секунд
 *
 * Тестовые данные:
 * - Уникальные имена:  User {VU_ID}-{TIMESTAMP}
 * - Уникальные email:  user{VU_ID}_{TIMESTAMP}@test.com
 * - Случайные пароли:  Password{RANDOM_NUM}
 *
 * Критерии успеха:
 * - 95-й перцентиль задержки < 1000 мс (abortOnFail)
 * - Уровень ошибок < 5%
 *
 * Собираемые метрики:
 * - vus:                Текущее количество виртуальных пользователей
 * - http_req_duration:  Время выполнения запросов (p95, p99)
 * - http_req_failed:    Процент неудачных запросов
 * - iterations:         Количество выполненных итераций
 * - data_received:      Объем полученных данных
 *
 * Генерация отчетов:
 * - JSON-дамп:         reports/summary.json
 * - Консольный вывод:  Статистика в текстовом виде
 *
 * Особенности теста:
 * - Автоматическая остановка при нарушении SLA
 * - Пауза 1 секунда между итерациями
 * - Проверка корректности созданных пользователей
 */

import http from 'k6/http';
import { check } from 'k6';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';

function generateUser(vuId) {
    return {
        fullName: `User ${vuId}-${Date.now()}`,
        email: `user${vuId}_${Date.now()}@test.com`,
        password: `Password${Math.floor(Math.random() * 1000)}`
    };
}

export const options = {
    scenarios: {
        ramp_up_test: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '2m', target: 10 },
                { duration: '1m', target: 20 },
                { duration: '30s', target: 30 },
            ],
            gracefulRampDown: '30s'
        }
    },
    thresholds: {
        http_req_duration: [
            { threshold: 'p(95)<1000', abortOnFail: true }
        ],
        http_req_failed: [
            { threshold: 'rate<0.05', abortOnFail: false }
        ]
    }
};

export default function () {
    const user = generateUser(__VU);

    const payload = JSON.stringify({
        fullName: user.fullName,
        email: user.email,
        password: user.password
    });

    const headers = {
        'Content-Type': 'application/json',
        'User-Agent': 'k6-load-test'
    };

    const res = http.post(
        'http://localhost:8084/api/users/register',
        payload,
        {headers: headers,
        timeout: '15s',
            retries: 2}
    );

    check(res, {
        'status is 201': (r) => r.status === 201,
        'has user ID': (r) => r.json().id !== undefined,
        'email matches': (r) => r.json().email === user.email
    });

}

    export function handleSummary(data) {
        const format = (num, decimals = 2) =>
            typeof num === 'number' ? num.toFixed(decimals) : 'N/A';

        const getStageMetrics = (stageName) => {
            return {
                vus: data.metrics[`vus{scenario:ramp_up_test}`]?.values?.max || 0,
                rps: data.metrics[`http_reqs{scenario:ramp_up_test}`]?.values?.rate || 0,
                p95: data.metrics[`http_req_duration{scenario:ramp_up_test}`]?.values?.['p(95)'] || 0,
                errors: data.metrics[`http_req_failed{scenario:ramp_up_test}`]?.values?.rate || 0,
                iterations: data.metrics[`iterations{scenario:ramp_up_test}`]?.values?.count || 0
            };
        };

        const stages = {
            '0-10 VU': getStageMetrics('ramp_up_test'),
            '10-20 VU': getStageMetrics('ramp_up_test'),
            '20-30 VU': getStageMetrics('ramp_up_test')
        };

        const bottlenecks = [];
        if (data.metrics.http_req_duration.values['p(95)'] > 1000) {
            bottlenecks.push(`- **p95 превышает SLA 1000мс** (${format(data.metrics.http_req_duration.values['p(95)'])}мс)`);
        }
        if (data.metrics.http_req_failed.values.rate > 0.05) {
            bottlenecks.push(`- **Уровень ошибок высокий** (${format(data.metrics.http_req_failed.values.rate * 100)}%)`);
        }
        if (data.metrics.iterations.values.count < 500) {
            bottlenecks.push(`- **Низкая производительность**: выполнено только ${data.metrics.iterations.values.count} итераций`);
        }

        const mdReport = `# Отчет по тесту RAMP-UP регистрации пользователей

- **Всего запросов:** ${data.metrics.http_reqs.values.count}
- **Длительность теста:** ${(data.state.testRunDurationMs / 1000).toFixed(0)} сек

## Результаты по этапам нагрузки
| Этап       | VUs  | RPS   | p95 (мс) | Ошибки | Итерации |
|------------|------|-------|----------|--------|----------|
| 0-10 VU    | 10   | ${format(stages['0-10 VU'].rps)} | ${format(stages['0-10 VU'].p95)} | ${format(stages['0-10 VU'].errors * 100)}% | ${stages['0-10 VU'].iterations} |
| 10-20 VU   | 20   | ${format(stages['10-20 VU'].rps)} | ${format(stages['10-20 VU'].p95)} | ${format(stages['10-20 VU'].errors * 100)}% | ${stages['10-20 VU'].iterations} |
| 20-30 VU   | 30   | ${format(stages['20-30 VU'].rps)} | ${format(stages['20-30 VU'].p95)} | ${format(stages['20-30 VU'].errors * 100)}% | ${stages['20-30 VU'].iterations} |

## Итоговые метрики
- **Средний RPS:** ${format(data.metrics.http_reqs.values.rate)}
- **Общий p95 latency:** ${format(data.metrics.http_req_duration.values['p(95)'])} мс
- **Максимальная задержка:** ${format(data.metrics.http_req_duration.values.max)} мс
- **Уровень успешных ответов:** ${format((1 - data.metrics.http_req_failed.values.rate) * 100)}%
- **Всего создано пользователей:** ${data.metrics.iterations.values.count}

## Узкие места
${bottlenecks.length > 0 ? bottlenecks.join('\n') : '- Система выдерживает нагрузку согласно SLA'}
`;
        return {
            'stdout': textSummary(data, { indent: ' ', enableColors: true }),
            'reports/summary.json': JSON.stringify(data, null, 2),
            'reports/rampup-report.md': mdReport
        };
    }

