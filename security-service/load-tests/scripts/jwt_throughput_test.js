/**
 * THROUGHPUT TEST: Определение максимальной пропускной способности JWT-верификации
 *
 * Цель: Найти предельный RPS (запросов в секунду) без нарушения SLA
 *
 * Стратегия:
 * - Постепенно увеличиваем нагрузку до точки отказа
 * - Фиксируем RPS при допустимой задержке
 */
import http from 'k6/http';
import { check, group } from 'k6';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';
import { htmlReport } from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";
import * as jsrsasign from 'https://cdn.jsdelivr.net/npm/jsrsasign@8.0.20/lib/jsrsasign.min.js';

const BASE_URL = 'http://localhost:8085';
const SECRET = "testsecretkeyfortestpurposesonly1234567890";

const tokens = Array(1000).fill().map((_, i) => {
    const header = { alg: 'HS256', typ: 'JWT' };
    const payload = {
        sub: `user${i}`,
        iat: Math.floor(Date.now() / 1000),
        exp: Math.floor(Date.now() / 1000) + 3600
    };
    return jsrsasign.KJUR.jws.JWS.sign(
        "HS256",
        JSON.stringify(header),
        JSON.stringify(payload),
        SECRET
    );
});

export let options = {
    scenarios: {
        main_test: {
            executor: 'ramping-arrival-rate',
            startRate: 50,
            timeUnit: '1s',
            preAllocatedVUs: 100,
            maxVUs: 300,
            stages: [
                { target: 100, duration: '1m' },
                { target: 300, duration: '1m' },
                { target: 400, duration: '2m' },  // Основная нагрузка
                { target: 500, duration: '1m' }
            ],
        },
    },
    discardResponseBodies: true,
    noConnectionReuse: false,
    thresholds: {
        'http_req_duration{status:200}': ['p(95)<500'], // Жесткий SLA
        'http_req_failed': ['rate<0.01'],                // < 0.1% ошибок
        'http_reqs': ['rate>500']
    },
};

export default function () {
    const token = tokens[__VU % tokens.length];


    const response = http.post(
        `${BASE_URL}/api/security/verify`,
        token,
        {
            headers: {'Content-Type': 'text/plain'},
            timeout: '2s'
        }
    );

    check(response, {
        'Status is 200': (r) => r.status === 200,
        'Fast response': (r) => r.timings.duration < 500
    });
}


export function handleSummary(data) {
    const rates = data.metrics.http_reqs.values.rates || [0];
    const maxRPS = Math.max(...rates);
    const p95 = data.metrics.http_req_duration.values['p(95)'] || 0;

    return {
        'stdout': textSummary(data, {
            indent: ' ',
            enableColors: true,
            customSummary: `
            ## Throughput Results
            - Max RPS achieved: ${Math.round(maxRPS)}/s
            - 95% latency: ${Math.round(p95)}ms
            `
        }),
        'reports/throughput-summary.json': JSON.stringify(data, null, 2),
        'reports/throughput-report.html': htmlReport(data)
    };
}