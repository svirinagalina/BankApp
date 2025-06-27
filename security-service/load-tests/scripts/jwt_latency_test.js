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
 * - Тело:              JSON с claims (sub, iat, exp)
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
 * - reports/latency-report.html:  Визуализированный HTML-отчёт
 *
 * Пример интерпретации:
 * ----------------------------------
 * http_req_duration p(95)=40.31ms означает, что 95% запросов
 * выполняются быстрее 40.31 мс.
 */
import http from 'k6/http';
import { check } from 'k6';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';
import { htmlReport } from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";

export let options = {
    vus: 1,
    iterations: 500,
    discardResponseBodies: true,
    noConnectionReuse: true,
};

const validToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0LXVzZXIiLCJpYXQiOjE3NTA2NTU4MjEsImV4cCI6MTc1MDY2MzAyMX0.AgBP2KTLmyOBGr1ioym0lCzK4Dw1RXBHfluLMsMNZQk";

export default function () {
    let response = http.post(
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
        'reports/latency-summary.json': JSON.stringify(data, null, 2),
        'reports/latency-report.html': htmlReport(data)
    };
}