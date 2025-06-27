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
 * - HTML-отчет:        reports/report.html
 * - JSON-дамп:         reports/summary.json
 * - Консольный вывод:  Статистика в текстовом виде
 *
 * Особенности теста:
 * - Автоматическая остановка при нарушении SLA
 * - Пауза 1 секунда между итерациями
 * - Проверка корректности созданных пользователей
 */

import http from 'k6/http';
import { check, sleep } from 'k6';
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

   // sleep(1);

}

    export function handleSummary(data) {
        return {
            'stdout': textSummary(data, { indent: ' ', enableColors: true }),
            'reports/summary.json': JSON.stringify(data, null, 2),
            'reports/report.html': htmlReport(data)
        };
    }
function htmlReport(data) {
    return `
  <!DOCTYPE html>
  <html>
  <head>
    <title>K6 Report</title>
    <style>
      body { font-family: Arial; margin: 20px; }
      pre { background: #f5f5f5; padding: 10px; }
    </style>
  </head>
  <body>
    <h1>Load Test Report</h1>
    <pre>${JSON.stringify(data.metrics, null, 2)}</pre>
  </body>
  </html>`;
}
