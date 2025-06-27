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
import { htmlReport } from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";

const validToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0LXVzZXIiLCJpYXQiOjE3NTA2NTU4MjEsImV4cCI6MTc1MDY2MzAyMX0.AgBP2KTLmyOBGr1ioym0lCzK4Dw1RXBHfluLMsMNZQk";

export let options = {
    stages: [
        { duration: '30s', target: 50 },  // Плавный рост до 50 VU
        { duration: '30s', target: 70 },
        { duration: '1m', target: 100 }, // Рост до 100 VU
        { duration: '1m', target: 100 }, // Пиковая нагрузка
    ],
    discardResponseBodies: true, //игнорирует тела всех HTTP-ответов, не сохраняя их в память
    noConnectionReuse: true, //Принудительно закрывает TCP-соединение после каждого запроса
    thresholds: {
        'http_req_duration{type:all}': ['p(95)<300'], // SLA
        'http_req_failed': ['rate<0.01'],
    }
};

export default function () {
    const response = http.post(
        'http://localhost:8085/api/security/verify',
        validToken,
        { headers: { 'Content-Type': 'application/json' } }
    );

    check(response, {
        'Status is 200': (r) => r.status === 200,
    });
}

export function handleSummary(data) {
    return {
        'stdout': textSummary(data, { indent: ' ', enableColors: true }),
        'reports/rampup-summary.json': JSON.stringify(data, null, 2),
        'reports/rampup-report.html': htmlReport(data)
    };
}