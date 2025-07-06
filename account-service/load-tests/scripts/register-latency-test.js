/**
 * LATENCY TEST: Измерение времени отклика API для одиночных запросов
 *
 * Цель: Определить базовую производительность endpoint'а без нагрузки.
 * Метрика: HTTP-задержка (latency) при регистрации пользователя.
 *
 * Параметры теста:
 * ----------------------------------
 * Тип теста:            Одиночные запросы (latency)
 * Endpoint:             POST /api/users/register
 *
 * Нагрузочный профиль:
 * - Виртуальных пользователей (VUs):    1
 * - Всего итераций:                     500
 * - Повторное использование соединения: отключено (noConnectionReuse)
 *
 * Критерии успеха:
 * - 95-й перцентиль задержки < 500 мс
 * - 0% ошибок
 *
 * Метрики:
 * - http_req_duration: Общее время запроса
 * - http_req_waiting:  Время ожидания ответа сервера
 * - iteration_duration: Полное время итерации
 *
 * Отчёты:
 * - reports/latency-summary.json: сырые данные
 */

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LatencyTest {
    private static final int ITERATIONS = 500;
    private static final String ENDPOINT = "http://localhost:8084/api/users/register";
    private static final int SLA_P95_MS = 500;
    private static final int CRITICAL_LATENCY_MS = 1000;
    private static final DecimalFormat df = new DecimalFormat("0.00");

    private static class TestResult {
    long duration;
    int statusCode;
    boolean isSuccess;
}

public static void main(String[] args) throws InterruptedException {
    List<Long> latencies = new ArrayList<>();
    List<TestResult> results = new ArrayList<>();
    int errorCount = 0;

    long testStartTime = System.currentTimeMillis();

    for (int i = 0; i < ITERATIONS; i++) {
        TestResult result = executeRequest(i);
        results.add(result);
        if (!result.isSuccess) {
            errorCount++;
        } else {
            latencies.add(result.duration);
        }
        // Небольшая пауза между запросами
        TimeUnit.MILLISECONDS.sleep(50);
    }

    long testDurationMs = System.currentTimeMillis() - testStartTime;

    generateReport(results, latencies, errorCount, testDurationMs);
}

private static TestResult executeRequest(int iteration) {
    TestResult result = new TestResult();
    long startTime = System.currentTimeMillis();

    try {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(ENDPOINT);

        String payload = String.format(
            "{\"fullName\":\"Test User %d\",\"email\":\"test%d@example.com\",\"password\":\"test123\"}",
            1, iteration);

        StringEntity entity = new StringEntity(payload);
        post.setEntity(entity);
        post.setHeader("Content-type", "application/json");
        post.setHeader("Connection", "close"); // Отключаем повторное использование соединения

        HttpResponse response = httpClient.execute(post);
        result.statusCode = response.getStatusLine().getStatusCode();
        result.isSuccess = result.statusCode >= 200 && result.statusCode < 300;
        EntityUtils.consume(response.getEntity()); // Освобождаем ресурсы
    } catch (IOException e) {
        result.isSuccess = false;
    } finally {
        result.duration = System.currentTimeMillis() - startTime;
    }

    return result;
}

private static void generateReport(List<TestResult> results, List<Long> latencies, int errorCount, long testDurationMs) {
    // Рассчитываем метрики
    double avgLatency = latencies.stream().mapToLong(l -> l).average().orElse(0);
    long maxLatency = latencies.stream().mapToLong(l -> l).max().orElse(0);
    double p95 = calculatePercentile(latencies, 95);
    double errorRate = (double) errorCount / results.size() * 100;

    // Рассчитываем среднее время ожидания (в этом упрощенном тесте принимаем равным latency)
    double avgWaiting = avgLatency;

    // Определяем узкие места
    List<String> bottlenecks = new ArrayList<>();
    if (p95 > SLA_P95_MS) bottlenecks.add("- **Превышен p95 latency** (SLA: <500мс)");
    if (maxLatency > CRITICAL_LATENCY_MS) bottlenecks.add("- **Критические выбросы задержки** (>1000мс)");
    if (errorCount > 0) bottlenecks.add(String.format("- **Ошибки запросов**: %d (%.2f%%)", errorCount, errorRate));
    if (avgWaiting > avgLatency * 0.7) bottlenecks.add("- **Высокое время ожидания ответа сервера**");

    // Формируем отчет
    String mdReport = String.format(
        "# Отчет LATENCY-теста регистрации пользователей\n\n" +
        "## Ключевые метрики\n" +
        "| Метрика               | Значение       |\n" +
        "|-----------------------|----------------|\n" +
        "| Среднее время ответа | %s мс |\n" +
        "| 95-й перцентиль (p95) | %s мс |\n" +
        "| Максимальная задержка | %d мс |\n" +
        "| Время ожидания (waiting) | %s мс |\n" +
        "| Успешных запросов    | %d/%d |\n" +
        "| Уровень ошибок       | %.2f%% |\n" +
        "| Всего запросов      | %d |\n" +
        "| Длительность теста  | %.1f сек |\n\n" +
        "## Узкие места\n%s",
        df.format(avgLatency),
        df.format(p95),
        maxLatency,
        df.format(avgWaiting),
        results.size() - errorCount, results.size(),
        errorRate,
        results.size(),
        testDurationMs / 1000.0,
        bottlenecks.size() > 0 ? String.join("\n", bottlenecks) : "- Система работает стабильно, узких мест не обнаружено"
    );

    // Сохраняем отчеты
    try {
        FileWriter jsonWriter = new FileWriter("reports/latency-summary.json");
        jsonWriter.write(String.format(
            "{\"metrics\": {\"http_reqs\": {\"count\": %d}, \"http_req_duration\": {\"avg\": %.2f, \"p95\": %.2f, \"max\": %d}, \"http_req_failed\": {\"count\": %d, \"rate\": %.4f}}}",
            results.size(), avgLatency, p95, maxLatency, errorCount, errorRate / 100
        ));
        jsonWriter.close();

        FileWriter mdWriter = new FileWriter("reports/latency-report.md");
        mdWriter.write(mdReport);
        mdWriter.close();

        System.out.println("Тест завершен. Отчеты сохранены в папке reports/");
    } catch (IOException e) {
        System.err.println("Ошибка при сохранении отчетов: " + e.getMessage());
    }
}

private static double calculatePercentile(List<Long> latencies, double percentile) {
    if (latencies.isEmpty()) return 0;

    Collections.sort(latencies);
    int index = (int) Math.ceil(percentile / 100.0 * latencies.size());
    return latencies.get(index - 1);
}
}