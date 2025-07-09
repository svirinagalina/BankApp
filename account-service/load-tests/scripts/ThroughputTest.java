/**
 * Throughput тест пропускной способности API регистрации пользователей.
 *
 * <p><b>Цель теста:</b> Определить максимальную устойчивую пропускную способность системы
 * при динамическом регулировании нагрузки и проверить соответствие SLA.</p>
 *
 * <h2>Параметры тестирования</h2>
 * - Тестируемый эндпоинт: POST /api/users/register<br>
 * - Целевой RPS: 5 запросов/сек<br>
 * - Максимальный допустимый RPS: 10 запросов/сек<br>
 * - Минимальное количество VU: 5<br>
 * - Максимальное количество VU: 10<br>
 * - Длительность теста: 60 секунд<br>
 * - Таймаут запроса: 15 секунд<br>
 *
 * <h2>Методика тестирования</h2>
 * <ol>
 *   <li>Динамическое регулирование нагрузки:
 *     <ul>
 *       <li>Увеличение количества VU при RPS ниже целевого</li>
 *       <li>Уменьшение количества VU при RPS выше максимального</li>
 *     </ul>
 *   </li>
 *   <li>Каждый виртуальный пользователь (VU):
 *     <ul>
 *       <li>Генерирует уникальные тестовые данные</li>
 *       <li>Отправляет POST-запрос с JSON-телом</li>
 *       <li>Проверяет код ответа (ожидается 201 Created)</li>
 *       <li>Измеряет время выполнения запроса</li>
 *     </ul>
 *   </li>
 *   <li>Мониторинг ключевых показателей каждую секунду</li>
 * </ol>
 *
 * <h2>Ключевые метрики</h2>
 * <ul>
 *   <li><b>Пропускная способность:</b>
 *     <ul>
 *       <li>Фактический RPS</li>
 *       <li>Соответствие целевому RPS</li>
 *     </ul>
 *   </li>
 *   <li><b>Производительность:</b>
 *     <ul>
 *       <li>Среднее время ответа</li>
 *       <li>95-й перцентиль времени ответа (p95)</li>
 *       <li>Максимальное время ответа</li>
 *     </ul>
 *   </li>
 *   <li><b>Надежность:</b>
 *     <ul>
 *       <li>Уровень ошибок</li>
 *       <li>Количество успешных/неудачных запросов</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h2>SLA критерии</h2>
 * - p95 latency ≤ 1000 мс<br>
 * - Уровень ошибок ≤ 5%<br>
 * - Достижение целевого RPS (5 запросов/сек)<br>
 */

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ThroughputTest {
    private static final String ENDPOINT = "http://localhost:8084/api/users/register";
    private static final int TARGET_RPS = 5;
    private static final int MAX_RPS = 10;
    private static final int MIN_VUS = 5;
    private static final int MAX_VUS = 10;
    private static final int TEST_DURATION_SEC = 60;
    private static final long REQUEST_TIMEOUT_MS = 15_000;
    private static final long P95_THRESHOLD_MS = 1000;
    private static final double MAX_ERROR_RATE = 0.05;

    private static final AtomicInteger successCount = new AtomicInteger();
    private static final AtomicInteger errorCount = new AtomicInteger();
    private static final AtomicInteger currentVUs = new AtomicInteger(MIN_VUS);
    private static final List<Long> latencies = new CopyOnWriteArrayList<>();
    private static volatile boolean abortTest = false;

    public static void main(String[] args) throws Exception {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(MAX_VUS);
        ScheduledExecutorService monitor = Executors.newSingleThreadScheduledExecutor();

        final long testStartTime = System.currentTimeMillis();
        monitor.scheduleAtFixedRate(() -> {
            double currentRps = (successCount.get() + errorCount.get()) * 1000.0 /
                    (System.currentTimeMillis() - testStartTime);
            double errorRate = getCurrentErrorRate();
            long p95 = calculateCurrentP95();

            System.out.printf(
                    "[Прогресс] RPS: %.2f, VUs: %d, Успешных: %d, Ошибок: %d, p95: %dмс%n",
                    currentRps, currentVUs.get(), successCount.get(), errorCount.get(), p95
            );

            if (p95 > P95_THRESHOLD_MS) {
                System.err.println("Превышен p95 latency: " + p95 + " мс");
            }

            if (errorRate > MAX_ERROR_RATE) {
                System.err.printf("Превышен уровень ошибок: %.2f%%%n", errorRate * 100);
            }

            if (currentRps < TARGET_RPS && currentVUs.get() < MAX_VUS) {
                currentVUs.incrementAndGet();
            } else if (currentRps > MAX_RPS && currentVUs.get() > MIN_VUS) {
                currentVUs.decrementAndGet();
            }
        }, 1, 1, TimeUnit.SECONDS);

        long testEndTime = testStartTime + TEST_DURATION_SEC * 1000;

        while (System.currentTimeMillis() < testEndTime && !abortTest) {
            int requestsToSend = (int) (TARGET_RPS / (double) currentVUs.get());

            for (int i = 0; i < currentVUs.get(); i++) {
                final int vuId = i + 1;
                executor.submit(() -> {
                    try {
                        sendRequest(vuId);
                        successCount.incrementAndGet();
                        Thread.sleep(1000 / requestsToSend);
                    } catch (Exception e) {
                        errorCount.incrementAndGet();
                    }
                });
            }

            Thread.sleep(1000);
        }

        executor.shutdown();
        monitor.shutdown();

        generateReport(testStartTime);
    }

    private static void sendRequest(int vuId) throws IOException {
        String payload = generateTestData(vuId);
        long startTime = System.nanoTime();

        HttpURLConnection connection = (HttpURLConnection) new URL(ENDPOINT).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("X-Debug", "true");
        connection.setDoOutput(true);
        connection.setConnectTimeout((int) REQUEST_TIMEOUT_MS);
        connection.setReadTimeout((int) REQUEST_TIMEOUT_MS);

        try (OutputStream os = connection.getOutputStream()) {
            os.write(payload.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = connection.getResponseCode();
        long latencyMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
        latencies.add(latencyMs);

        if (responseCode != 201) {
            String errorBody = new String(connection.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
            System.err.printf("ERROR %d: %s%nRequest data: %s%n",
                    responseCode, errorBody, payload);
            throw new IOException("Invalid status code: " + responseCode);
        }
    }

    private static String generateTestData(int vuId) {
        long timestamp = System.currentTimeMillis();
        return String.format(
                "{\"fullName\":\"User %d-%d\",\"email\":\"test.%d.%d@test.com\",\"password\":\"ValidPass123!%d\"}",
                vuId, timestamp, vuId, timestamp, vuId
        );
    }

    private static double getCurrentErrorRate() {
        int total = successCount.get() + errorCount.get();
        return total > 0 ? (double) errorCount.get() / total : 0;
    }

    private static long calculateCurrentP95() {
        if (latencies.isEmpty()) return 0;

        List<Long> sorted = new ArrayList<>(latencies);
        Collections.sort(sorted);
        int index = (int) Math.ceil(0.95 * sorted.size()) - 1;
        return sorted.get(Math.max(0, index));
    }

    private static void generateReport(long testStartTime) {
        long testDurationMs = System.currentTimeMillis() - testStartTime;
        int totalRequests = successCount.get() + errorCount.get();
        double actualRps = totalRequests / (testDurationMs / 1000.0);
        double errorRate = getCurrentErrorRate();
        long p95 = calculateCurrentP95();
        long maxLatency = latencies.stream().max(Long::compare).orElse(0L);

        List<String> bottlenecks = new ArrayList<>();
        if (actualRps < TARGET_RPS) {
            bottlenecks.add(String.format("- Низкий RPS (%.2f при целевом %d)", actualRps, TARGET_RPS));
        }
        if (p95 > P95_THRESHOLD_MS) {
            bottlenecks.add(String.format("- Превышен p95 latency (%d мс при SLA %d мс)", p95, P95_THRESHOLD_MS));
        }
        if (errorRate > MAX_ERROR_RATE) {
            bottlenecks.add(String.format("- Высокий уровень ошибок (%.2f%% при допустимом %.2f%%)",
                    errorRate * 100, MAX_ERROR_RATE * 100));
        }
        if (currentVUs.get() >= MAX_VUS) {
            bottlenecks.add(String.format("- Достигнут максимум виртуальных пользователей (%d VUs)", currentVUs.get()));
        }

        String mdReport = String.format(
                "# Отчёт по тесту пропускной способности API регистрации%n%n" +
                        "## Ключевые метрики%n" +
                        "| Метрика               | Значение          |%n" +
                        "|------------------------|-------------------|%n" +
                        "| Фактический RPS       | %.2f |%n" +
                        "| Успешных запросов     | %d (%.2f%%) |%n" +
                        "| Среднее время ответа  | %.2f мс |%n" +
                        "| p95 время ответа      | %d мс |%n" +
                        "| Максимальная задержка | %d мс |%n" +
                        "| Уровень ошибок        | %.2f%% |%n" +
                        "| Использовано VUs      | %d |%n" +
                        "| Целевой RPS           | %d запр/сек |%n" +
                        "| Длительность          | %.1f сек|%n%n" +
                        "## Узкие места%n%s%n",
                actualRps,
                successCount.get(), (1 - errorRate) * 100,
                latencies.stream().mapToLong(Long::longValue).average().orElse(0),
                p95,
                maxLatency,
                errorRate * 100,
                currentVUs.get(),
                TARGET_RPS,
                testDurationMs / 1000.0,
                bottlenecks.isEmpty() ? "- Все показатели соответствуют SLA" :
                        String.join("%n", bottlenecks)
        );

        String jsonReport = String.format(
                "{%n" +
                        "  \"metrics\": {%n" +
                        "    \"requests\": {%n" +
                        "      \"total\": %d,%n" +
                        "      \"successful\": %d,%n" +
                        "      \"failed\": %d%n" +
                        "    },%n" +
                        "    \"rps\": %.2f,%n" +
                        "    \"latency\": {%n" +
                        "      \"avg\": %.2f,%n" +
                        "      \"p95\": %d,%n" +
                        "      \"max\": %d%n" +
                        "    },%n" +
                        "    \"error_rate\": %.4f,%n" +
                        "    \"vus\": %d%n" +
                        "  },%n" +
                        "  \"thresholds\": {%n" +
                        "    \"target_rps\": %d,%n" +
                        "    \"p95_latency\": %d,%n" +
                        "    \"max_error_rate\": %.2f%n" +
                        "  }%n" +
                        "}",
                totalRequests,
                successCount.get(),
                errorCount.get(),
                actualRps,
                latencies.stream().mapToLong(Long::longValue).average().orElse(0),
                p95,
                maxLatency,
                errorRate,
                currentVUs.get(),
                TARGET_RPS,
                P95_THRESHOLD_MS,
                MAX_ERROR_RATE
        );

        System.out.println(mdReport);

        try {
            Path currentDir = Path.of(LatencyTest.class.getProtectionDomain()
                            .getCodeSource().getLocation().toURI())
                    .getParent();

            Path loadTestsDir = currentDir.getParent();
            Path reportPath = loadTestsDir.resolve("reports/throughput-report.md");

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
            Path reportPath = loadTestsDir.resolve("reports/throughput-summary.json");

            Files.createDirectories(reportPath.getParent());
            Files.write(reportPath, jsonReport.getBytes(StandardCharsets.UTF_8));

            System.out.println("Отчет сохранен: " + reportPath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Не удалось сохранить отчет: " + e.getMessage());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}