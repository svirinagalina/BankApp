/**
 * LATENCY тест для измерения задержек (latency) REST API эндпоинта регистрации пользователей.
 *
 * <p><b>Цель теста:</b> определение стабильности времени отклика API при последовательных запросах.</p>
 *
 * <h2>Параметры тестирования</h2>
 * - Тестируемый эндпоинт: POST /api/users/register<br>
 * - Количество итераций: 500<br>
 * - Виртуальных пользователей: 1 (последовательные запросы)<br>
 * - Таймаут запроса: 10 секунд<br>
 * - Пауза между запросами: 50 мс<br>
 *
 * <h2>Методика тестирования</h2>
 * <ol>
 *   <li>Генерация уникальных тестовых данных для каждой итерации</li>
 *   <li>Последовательная отправка POST-запросов с JSON-телом</li>
 *   <li>Фиксация времени выполнения каждого запроса</li>
 *   <li>Принудительный вызов сборщика мусора между итерациями</li>
 *   <li>Сбор статистики по задержкам и ошибкам</li>
 * </ol>
 *
 * <h2>Измеряемые метрики</h2>
 * <ul>
 *   <li><b>Основные метрики:</b>
 *     <ul>
 *       <li>Среднее время ответа (avg latency)</li>
 *       <li>95-й перцентиль времени ответа (p95)</li>
 *       <li>Максимальное время ответа</li>
 *     </ul>
 *   </li>
 *   <li><b>Дополнительные метрики:</b>
 *     <ul>
 *       <li>Общее время выполнения итерации</li>
 *       <li>Количество и процент ошибок</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h2>SLA критерии</h2>
 * - p95 latency: менее 500 мс<br>
 * - Уровень ошибок: 0%<br>
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class LatencyTest {
    private static final String ENDPOINT = "http://localhost:8084/api/users/register";
    private static final int ITERATIONS = 500;
    private static final long P95_THRESHOLD_MS = 500;
    private static final long REQUEST_TIMEOUT_MS = 30_000;

    private static final List<Long> latencies = new ArrayList<>();
    private static final List<Long> waitingTimes = new ArrayList<>();
    private static final List<Long> iterationDurations = new ArrayList<>();
    private static int errorCount = 0;

    public static void main(String[] args) throws Exception {
        runLatencyTest();
        generateReports();
    }

    private static void runLatencyTest() throws InterruptedException {
        for (int i = 0; i < ITERATIONS; i++) {
            long iterationStart = System.nanoTime();

            try {
                long requestStart = System.nanoTime();
                sendRequest(i);
                long requestEnd = System.nanoTime();

                long iterationDuration = TimeUnit.NANOSECONDS.toMillis(requestEnd - iterationStart);
                long requestDuration = TimeUnit.NANOSECONDS.toMillis(requestEnd - requestStart);

                latencies.add(requestDuration);
                iterationDurations.add(iterationDuration);
                waitingTimes.add(requestDuration);

            } catch (IOException e) {
                errorCount++;
                System.err.println("Request failed: " + e.getMessage());
            }

            System.gc();
            Thread.sleep(50);
        }
    }

    private static void sendRequest(int iteration) throws IOException {
        String payload = String.format(
                "{\"fullName\":\"Test User 1\",\"email\":\"test%d@example.com\",\"password\":\"test123\"}",
                iteration
        );

        String authString = "user:03b8466f-5af7-46b4-a12c-b64722e6b18f";
        String authHeaderValue = "Basic " + Base64.getEncoder().encodeToString(authString.getBytes());

        HttpURLConnection connection = (HttpURLConnection) new URL(ENDPOINT).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", authHeaderValue);  // Добавляем заголовок авторизации
        connection.setDoOutput(true);
        connection.setConnectTimeout((int) REQUEST_TIMEOUT_MS);
        connection.setReadTimeout((int) REQUEST_TIMEOUT_MS);
        connection.setRequestProperty("Connection", "close");

        try (OutputStream os = connection.getOutputStream()) {
            os.write(payload.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = connection.getResponseCode();
        if (responseCode < 200 || responseCode >= 300) {
            String errorBody = "";
            try (InputStream errorStream = connection.getErrorStream()) {
                if (errorStream != null) {
                    errorBody = new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);
                }
            }
            throw new IOException("HTTP error code: " + responseCode + ". Response: " + errorBody);
        }
    }


    private static void generateReports() throws IOException {
        Collections.sort(latencies);
        Collections.sort(waitingTimes);
        Collections.sort(iterationDurations);

        double avgLatency = calculateAverage(latencies);
        long p95Latency = calculatePercentile(latencies, 95);
        long maxLatency = latencies.isEmpty() ? 0 : latencies.get(latencies.size() - 1);
        double errorRate = (double) errorCount / ITERATIONS * 100;

        List<String> bottlenecks = detectBottlenecks(p95Latency, maxLatency, errorRate);

        String mdReport = String.format(
                "# Отчет LATENCY теста\n\n" +
                        "## Основные параметры теста\n" +
                        "| Параметр               | Значение          |\n" +
                        "|------------------------|------------------|\n" +
                        "| Endpoint              | %s |\n" +
                        "| Итерации              | %d |\n" +
                        "| Виртуальных пользователей | 1 |\n" +
                        "| Всего запросов        | %d |\n" +
                        "| Ошибки                | %d (%.2f%%) |\n\n" +
                        "## Ключевые метрики\n" +
                        "- **Средняя задержка:** %.2f мс\n" +
                        "- **p95 задержки:** %d мс\n" +
                        "- **Максимальная задержка:** %d мс\n" +
                        "- **Пороговое значение (p95):** < %d мс\n\n" +
                        "## Узкие места\n%s\n",
                ENDPOINT,
                ITERATIONS,
                ITERATIONS,
                errorCount,
                errorRate,
                avgLatency,
                p95Latency,
                maxLatency,
                P95_THRESHOLD_MS,
                bottlenecks.isEmpty() ?
                        "- Узких мест не обнаружено" :
                        String.join("\n", bottlenecks)
        );

        String jsonReport = String.format(
                "{\n" +
                        "  \"metrics\": {\n" +
                        "    \"iterations\": %d,\n" +
                        "    \"errors\": %d,\n" +
                        "    \"error_rate\": %.4f,\n" +
                        "    \"latency\": {\n" +
                        "      \"avg\": %.2f,\n" +
                        "      \"p95\": %d,\n" +
                        "      \"max\": %d\n" +
                        "    },\n" +
                        "    \"threshold\": %d\n" +
                        "  }\n" +
                        "}",
                ITERATIONS,
                errorCount,
                errorRate,
                avgLatency,
                p95Latency,
                maxLatency,
                P95_THRESHOLD_MS
        );

        System.out.println(mdReport);

        try {
            Path currentDir = Path.of(LatencyTest.class.getProtectionDomain()
                            .getCodeSource().getLocation().toURI())
                    .getParent();

            Path loadTestsDir = currentDir.getParent();
            Path reportPath = loadTestsDir.resolve("reports/latency-report.md");

            Files.createDirectories(reportPath.getParent());
            Files.write(reportPath, mdReport.getBytes(StandardCharsets.UTF_8));

            System.out.println("Отчет сохранен: " + reportPath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Не удалось сохранить отчет: " + e.getMessage());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        try {
            Path currentDir = Path.of(LatencyTest.class.getProtectionDomain()
                            .getCodeSource().getLocation().toURI())
                    .getParent();

            Path loadTestsDir = currentDir.getParent();
            Path reportPath = loadTestsDir.resolve("reports/latency-summary.json");

            Files.createDirectories(reportPath.getParent());
            Files.write(reportPath, jsonReport.getBytes(StandardCharsets.UTF_8));

            System.out.println("Отчет сохранен: " + reportPath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Не удалось сохранить отчет: " + e.getMessage());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String> detectBottlenecks(long p95Latency, long maxLatency, double errorRate) {
        List<String> bottlenecks = new ArrayList<>();

        if (p95Latency > P95_THRESHOLD_MS) {
            bottlenecks.add(String.format("- **Превышен p95 latency**: %d мс при пороге %d мс",
                    p95Latency, P95_THRESHOLD_MS));
        }

        if (maxLatency > P95_THRESHOLD_MS * 2) {
            bottlenecks.add(String.format("- **Высокая максимальная задержка**: %d мс", maxLatency));
        }

        if (errorRate > 0) {
            bottlenecks.add(String.format("- **Обнаружены ошибки**: %d (%.2f%%)", errorCount, errorRate));
        }

        return bottlenecks;
    }

    private static long calculatePercentile(List<Long> data, int percentile) {
        if (data.isEmpty()) return 0;
        int index = (int) Math.ceil(percentile / 100.0 * data.size()) - 1;
        return data.get(Math.max(0, Math.min(index, data.size() - 1)));
    }

    private static double calculateAverage(List<Long> data) {
        if (data.isEmpty()) return 0;
        return data.stream().mapToLong(Long::longValue).average().orElse(0);
    }
}