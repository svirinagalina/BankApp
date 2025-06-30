/**
 * LATENCY TEST: Измерение времени отклика JWT-верификации
 *
 * Цель: Определить базовую производительность endpoint'а проверки JWT-токенов без нагрузки.
 * Метрика: HTTP-задержка (latency) при верификации валидного токена.
 *
 * Тестируемый endpoint:
 * ----------------------------------
 * Метод:               POST
 * URL:                 http://localhost:8085/api/security/verify
 * Content-Type:        application/json
 * Тело запроса:        Сырой JWT-токен (строка)
 *
 * Ожидаемый ответ:
 * - Статус:            200 OK
 *
 * Нагрузочный профиль:
 * ----------------------------------
 * Тип теста:           Одиночные запросы (latency)
 * Виртуальных пользователей (VUs):    1
 * Всего итераций:                     500
 * Повторное использование соединения: отключено (noConnectionReuse)
 * Сохранение тел ответов:             отключено (discardResponseBodies)
 *
 * Тестовые данные:
 * ----------------------------------
 * JWT-токен:           HS256-токен с payload:
 *                      {
 *                        "sub": "test-user",
 *                        "iat": 1750655821,
 *                        "exp": 1750663021
 *                      }
 * Секретный ключ:      testsecretkeyfortestpurposesonly1234567890
 *
 * Критерии успеха:
 * ----------------------------------
 * - 100% успешных ответов (Status 200)
 * - 95-й перцентиль задержки < 100 мс
 * - 0% ошибок верификации
 *
 * Ключевые метрики:
 * ----------------------------------
 * - http_req_duration:  Общее время запроса (отправка + обработка + получение)
 * - http_req_waiting:   Время обработки на сервере
 * - http_req_connecting: Время установки соединения
 * - iteration_duration: Полное время итерации (включая sleep)
 *
 * Генерация отчётов:
 * ----------------------------------
 * - stdout:             Текстовая сводка в консоли
 * - reports/latency-summary.json: Полные сырые данные в JSON
 *
 * Пример интерпретации:
 * ----------------------------------
 * http_req_duration p(95)=40.31ms означает, что 95% запросов
 * выполняются быстрее 40.31 мс.
 */
import http from 'k6/http';
import { check } from 'k6';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';
import * as jsrsasign from 'https://cdn.jsdelivr.net/npm/jsrsasign@8.0.20/lib/jsrsasign.min.js';

export let options = {
    vus: 1,
    iterations: 500,
    discardResponseBodies: true,
    noConnectionReuse: true,
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
    const getMetric = (metric, field, defaultValue = 0) => {
        return data.metrics[metric]?.values?.[field] ?? defaultValue;
    };

    const metrics = {
        duration: data.state?.testRunDurationMs ? (data.state.testRunDurationMs / 1000).toFixed(1) : 0,
        count: getMetric('http_reqs', 'count'),
        avg: getMetric('http_req_duration', 'avg'),
        max: getMetric('http_req_duration', 'max'),
        errors: getMetric('http_req_failed', 'count'),
        errorRate: getMetric('http_req_failed', 'rate')
    };

    const bottlenecks = [];
    if (metrics.avg > 50) bottlenecks.push("- Среднее время ответа превышает 50мс");
    if (metrics.max > 500) bottlenecks.push("- Обнаружены выбросы задержки (>500мс)");
    if (metrics.errors > 0) bottlenecks.push(`- Найдены ошибки: ${metrics.errors}`);

    //  Markdown
    const mdReport = `# Отчет по тестированию JWT верификации


## Результаты
| Метрика               | Значение       |
|-----------------------|----------------|
| Среднее время ответа | ${metrics.avg.toFixed(2)} мс |
| Максимальное время   | ${metrics.max.toFixed(2)} мс |
| Успешных запросов    | ${500 - metrics.errors}/${500} |
| Уровень ошибок       | ${(metrics.errorRate * 100).toFixed(2)}% |
| Длительность теста       | ${metrics.duration} сек |

## Узкие места
${bottlenecks.length > 0 ? bottlenecks.join('\n') : "- Система работает стабильно, узких мест не обнаружено"}
`;
    return {
        'stdout': textSummary(data, { indent: ' ', enableColors: true }),
        'reports/latency-summary.json': JSON.stringify(data, null, 2),
        'reports/latency-report.md': mdReport
    };
}