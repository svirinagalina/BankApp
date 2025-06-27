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
import { htmlReport } from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";
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
    vus: 50,                         // Оптимальная нагрузка из ramp-up теста
    duration: '30m',                  // Продолжительность теста
    discardResponseBodies: true,     // Экономия памяти
    noConnectionReuse: false,        // Реалистичный сценарий (keep-alive)
    thresholds: {
        'http_req_duration{type:all}': ['p(95)<400'], // Ослабленный SLA для длительного теста
        'http_req_failed': ['rate<0.001'],            // < 0.1% ошибок
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
    return {
        'stdout': textSummary(data, { indent: ' ', enableColors: true }),
        'reports/soak-summary.json': JSON.stringify(data, null, 2),
        'reports/soak-report.html': htmlReport(data, { title: "JWT Soak Test" })
    };
}
