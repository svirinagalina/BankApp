/**
 * SOAK-тест для проверки устойчивости сервиса регистрации пользователей при длительной нагрузке.
 *
 * <p><b>Цель теста:</b> выявление проблем с памятью, утечек ресурсов и деградации производительности
 * при продолжительной работе сервиса в условиях стабильной нагрузки.</p>
 *
 * <h2>Параметры тестирования</h2>
 * <ul>
 *   <li>Количество виртуальных пользователей: 5-10 (автоматическая регулировка)</li>
 *   <li>Продолжительность теста: 30 минут</li>
 *   <li>Целевая пропускная способность (RPS): 5 запросов/сек</li>
 *   <li>Шаг увеличения нагрузки: ±1 VU каждые 5 сек (на основе текущего RPS)</li>
 *   <li>Таймаут запроса: 10 секунд</li>
 * </ul>
 *
 * <h2>Методика тестирования</h2>
 * <ol>
 *   <li>Генерация уникальных тестовых данных для каждого запроса</li>
 *   <li>Автоматическая регулировка количества виртуальных пользователей для поддержания целевого RPS</li>
 *   <li>Отправка POST-запросов на эндпоинт /api/users/register</li>
 *   <li>Мониторинг метрик каждые 5 секунд</li>
 *   <li>Сравнение производительности в первой и последней четверти теста</li>
 * </ol>
 *
 * <h2>Ключевые метрики</h2>
 * <ul>
 *   <li><b>Производительность:</b>
 *     <ul>
 *       <li>p95 latency - 95-й перцентиль времени ответа</li>
 *       <li>Максимальная задержка</li>
 *       <li>Фактический RPS</li>
 *       <li>Деградация производительности (сравнение p95 первой и последней четверти)</li>
 *     </ul>
 *   </li>
 *   <li><b>Надежность:</b>
 *     <ul>
 *       <li>Уровень ошибок (%)</li>
 *       <li>Типы ошибок (HTTP коды)</li>
 *       <li>Стабильность работы под нагрузкой</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h2>SLA критерии</h2>
 * <table border="1">
 *   <tr><th>Метрика</th><th>Целевое значение</th><th>Критическое значение</th></tr>
 *   <tr><td>p95 latency</td><td>&lt; 300 мс</td><td>500 мс</td></tr>
 *   <tr><td>Уровень ошибок</td><td>&lt; 0.5%</td><td>1%</td></tr>
 *   <tr><td>Деградация производительности</td><td>&lt; 15%</td><td>30%</td></tr>
 *   <tr><td>Доступность RPS</td><td>100% целевого</td><td>80% целевого</td></tr>
 * </table>
 *
 * <h2>Анализ результатов</h2>
 * <ul>
 *   <li><b>Успешный тест:</b> все метрики в пределах SLA, деградация &lt; 15%</li>
 *   <li><b>Требуется оптимизация:</b> превышение SLA по p95 или уровню ошибок</li>
 *   <li><b>Критическая проблема:</b> деградация &gt; 30% или RPS &lt; 80% целевого</li>
 * </ul>
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
import java.util.concurrent.atomic.AtomicLong;

public class SoakTest {
    private static final String ENDPOINT = "http://localhost:8084/api/users/register";
    private static final int TARGET_RPS = 5;
    private static final int MIN_VUS = 5;
    private static final int MAX_VUS = 10;
    private static final int TEST_DURATION_MIN = 30;
    private static final long REQUEST_TIMEOUT_MS = 10_000;
    private static final long P95_THRESHOLD_MS = 500;
    private static final double MAX_ERROR_RATE = 0.01;

    private static final AtomicInteger successCount = new AtomicInteger();
    private static final AtomicInteger errorCount = new AtomicInteger();
    private static final AtomicInteger currentVUs = new AtomicInteger(MIN_VUS);
    private static final List<Long> latencies = new CopyOnWriteArrayList<>();
    private static final AtomicLong firstQuarterP95 = new AtomicLong();
    private static volatile boolean abortTest = false;

    public static void main(String[] args) throws Exception {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(MAX_VUS);
        ScheduledExecutorService monitor = Executors.newSingleThreadScheduledExecutor();

        final long testStartTime = System.currentTimeMillis();
        final long testEndTime = testStartTime + TEST_DURATION_MIN * 60 * 1000;

        monitor.scheduleAtFixedRate(() -> {
            double currentRps = (successCount.get() + errorCount.get()) * 1000.0 /
                    (System.currentTimeMillis() - testStartTime);
            double errorRate = getCurrentErrorRate();
            long p95 = calculateCurrentP95();

            if (firstQuarterP95.get() == 0 &&
                    System.currentTimeMillis() - testStartTime > TEST_DURATION_MIN * 60 * 1000 / 4) {
                firstQuarterP95.set(p95);
            }

            System.out.printf(
                    "[Прогресс] %dmin, RPS: %.2f, VUs: %d, Успешных: %d, Ошибок: %d, p95: %dмс%n",
                    (System.currentTimeMillis() - testStartTime) / 60000,
                    currentRps, currentVUs.get(), successCount.get(), errorCount.get(), p95
            );

            if (p95 > P95_THRESHOLD_MS) {
                System.err.println("Превышен p95 latency: " + p95 + " мс");
            }

            if (errorRate > MAX_ERROR_RATE) {
                System.err.printf("Превышен уровень ошибок: %.2f%%%n", errorRate * 100);
            }

            if (currentRps < TARGET_RPS * 0.8 && currentVUs.get() < MAX_VUS) {
                currentVUs.incrementAndGet();
            } else if (currentRps > TARGET_RPS * 1.2 && currentVUs.get() > MIN_VUS) {
                currentVUs.decrementAndGet();
            }
        }, 5, 5, TimeUnit.SECONDS);

        while (System.currentTimeMillis() < testEndTime && !abortTest) {
            int requestsPerVu = (int) Math.ceil(TARGET_RPS / (double) currentVUs.get());

            for (int i = 0; i < currentVUs.get(); i++) {
                final int vuId = i + 1;
                executor.submit(() -> {
                    try {
                        for (int j = 0; j < requestsPerVu; j++) {
                            sendRequest(vuId, j);
                            Thread.sleep(1000 / requestsPerVu);
                        }
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

    private static void sendRequest(int vuId, int iteration) throws IOException {
        String payload = generateTestData(vuId);
        long startTime = System.nanoTime();

        HttpURLConnection connection = (HttpURLConnection) new URL(ENDPOINT).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        connection.setConnectTimeout((int) REQUEST_TIMEOUT_MS);
        connection.setReadTimeout((int) REQUEST_TIMEOUT_MS);
        connection.setRequestProperty("Connection", "close");

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

        successCount.incrementAndGet();
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

        List<String> slaChecks = new ArrayList<>();
        if (p95 <= P95_THRESHOLD_MS) {
            slaChecks.add(String.format("**p95 latency в пределах SLA** (%dms)", p95));
        } else {
            slaChecks.add(String.format("**p95 latency превышает SLA 500ms** (%dms)", p95));
        }

        if (errorRate <= MAX_ERROR_RATE) {
            slaChecks.add(String.format("**Уровень ошибок в пределах нормы** (%.2f%%)", errorRate * 100));
        } else {
            slaChecks.add(String.format("**Уровень ошибок превышает 1%%** (%.2f%%)", errorRate * 100));
        }

        List<String> bottlenecks = new ArrayList<>();
        if (firstQuarterP95.get() > 0 && p95 > firstQuarterP95.get() * 1.5) {
            bottlenecks.add(String.format("- **Деградация производительности**: p95 вырос на %.2f%% за время теста",
                    ((double) p95 / firstQuarterP95.get() - 1) * 100));
        }

        if (errorRate > 0 && errorRate < MAX_ERROR_RATE) {
            bottlenecks.add(String.format("- **Накопление ошибок**: обнаружены спорадические ошибки (%d всего)", errorCount.get()));
        }

        if (actualRps < TARGET_RPS * 0.8) {
            bottlenecks.add(String.format("- **Нестабильный RPS**: средний %.2f при целевом %d", actualRps, TARGET_RPS));
        }

        String mdReport = String.format(
                "# Отчет SOAK-теста API регистрации пользователей%n%n" +
                        "## Основные параметры теста%n" +
                        "| Параметр               | Значение          |%n" +
                        "|------------------------|------------------|%n" +
                        "| Длительность          | %d минут         |%n" +
                        "| Целевой RPS          | %d запр/сек       |%n" +
                        "| Виртуальных пользователей | %d-%d VU      |%n" +
                        "| Всего запросов        | %d |%n" +
                        "| Средний RPS           | %.2f |%n%n" +
                        "## Ключевые метрики производительности%n" +
                        "- **p95 latency:** %d ms%n" +
                        "- **Максимальная задержка:** %d ms%n" +
                        "- **Уровень ошибок:** %.2f%%%n" +
                        "- **Использовано VUs:** %d%n%n" +
                        "## Проверка SLA%n%s%n%n" +
                        "## Узкие места (Bottlenecks)%n%s%n",
                TEST_DURATION_MIN,
                TARGET_RPS,
                MIN_VUS, MAX_VUS,
                totalRequests,
                actualRps,
                p95,
                maxLatency,
                errorRate * 100,
                currentVUs.get(),
                String.join("%n", slaChecks),
                bottlenecks.isEmpty() ? "- Критических узких мест не обнаружено" :
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
                        "    \"vus\": %d,%n" +
                        "    \"first_quarter_p95\": %d%n" +
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
                firstQuarterP95.get(),
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
            Path reportPath = loadTestsDir.resolve("reports/soak-report.md");

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
            Path reportPath = loadTestsDir.resolve("reports/soak-ssummary.json");

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
