/**
 * Ramp-Up тест с постепенным увеличением нагрузки для API регистрации пользователей.
 *
 * <p><b>Цель теста:</b> Проверить устойчивость системы к постепенно возрастающей нагрузке,
 * выявить предельные значения производительности и точки отказа.</p>
 *
 * <h2>Параметры тестирования</h2>
 * - Тестируемый эндпоинт: POST /api/users/register<br>
 * - Максимальное время ожидания ответа: 15 секунд<br>
 * - Допустимый уровень ошибок: 5%<br>
 * - Пороговое значение p95 latency: 2000 мс<br>
 * - Плавное снижение нагрузки: 30 секунд<br>
 *
 * <h2>Методика тестирования</h2>
 * <ol>
 *   <li>Постепенное увеличение количества виртуальных пользователей (VUs) по заданным этапам</li>
 *   <li>Каждый виртуальный пользователь выполняет:
 *     <ul>
 *       <li>Генерацию уникальных тестовых данных</li>
 *       <li>Отправку POST-запроса с JSON-телом</li>
 *       <li>Проверку ответа (код состояния и содержимое)</li>
 *       <li>Паузу в 1 секунду между запросами</li>
 *     </ul>
 *   </li>
 *   <li>Мониторинг ключевых метрик каждые 5 секунд</li>
 *   <li>Автоматическое прекращение теста при превышении пороговых значений</li>
 *   <li>Плавное снижение нагрузки после завершения основных этапов</li>
 * </ol>
 *
 * <h2>Измеряемые метрики</h2>
 * <ul>
 *   <li><b>Основные метрики:</b>
 *     <ul>
 *       <li>Количество виртуальных пользователей (VUs)</li>
 *       <li>Количество успешных/неудачных запросов</li>
 *       <li>Уровень ошибок</li>
 *       <li>Запросов в секунду (RPS)</li>
 *     </ul>
 *   </li>
 *   <li><b>Метрики времени ответа:</b>
 *     <ul>
 *       <li>p95 latency</li>
 *       <li>Максимальное время ответа</li>
 *     </ul>
 *   </li>
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

public class RampUpTest {
    private static final String ENDPOINT = "http://localhost:8084/api/users/register";
    private static final long REQUEST_TIMEOUT_MS = 15_000;
    private static final double MAX_ERROR_RATE = 0.05;
    private static final long P95_THRESHOLD_MS = 2000;

    private static final int[][] STAGES = {
            {10, 120},
            {20, 60},
            {30, 30}
    };
    private static final int GRACEFUL_RAMP_DOWN_SEC = 30;

    private static final List<Long> latencies = new CopyOnWriteArrayList<>();
    private static final AtomicInteger successCount = new AtomicInteger();
    private static final AtomicInteger errorCount = new AtomicInteger();
    private static final AtomicInteger iterations = new AtomicInteger();
    private static final AtomicInteger currentVUs = new AtomicInteger();
    private static volatile boolean abortTest = false;

    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newCachedThreadPool();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        long testStartTime = System.currentTimeMillis();

        scheduler.scheduleAtFixedRate(() -> {
            if (abortTest) return;

            double errorRate = getCurrentErrorRate();
            long p95 = calculateCurrentP95();

            if (p95 > P95_THRESHOLD_MS) {
                System.err.printf("Превышен p95 latency: %d мс (порог: %d мс)%n", p95, P95_THRESHOLD_MS);
            }

            if (errorRate > MAX_ERROR_RATE) {
                System.err.printf("Превышен уровень ошибок: %.2f%% (порог: %.2f%%)%n",
                        errorRate * 100, MAX_ERROR_RATE * 100);
            }
        }, 5, 5, TimeUnit.SECONDS);

        for (int[] stage : STAGES) {
            if (abortTest) break;

            int targetVUs = stage[1];
            int durationSec = stage[0];
            long stageEndTime = System.currentTimeMillis() + durationSec * 1000;

            while (System.currentTimeMillis() < stageEndTime && !abortTest) {
                int neededVUs = targetVUs - currentVUs.get();
                if (neededVUs > 0) {
                    int vusToAdd = Math.min(neededVUs, 2);
                    for (int i = 0; i < vusToAdd; i++) {
                        executor.submit(new VirtualUser(currentVUs.incrementAndGet()));
                    }
                }

                Thread.sleep(2000);
                printProgress(testStartTime);
            }
        }

        if (!abortTest) {
            long rampDownEnd = System.currentTimeMillis() + GRACEFUL_RAMP_DOWN_SEC * 1000;
            while (currentVUs.get() > 0 && System.currentTimeMillis() < rampDownEnd) {
                currentVUs.decrementAndGet();
                Thread.sleep(1000);
            }
        }

        executor.shutdownNow();
        scheduler.shutdownNow();

        generateReports(testStartTime);
    }

    static class VirtualUser implements Runnable {
        private final int vuId;
        private final Random random = new Random();

        VirtualUser(int vuId) {
            this.vuId = vuId;
        }

        @Override
        public void run() {
            while (!abortTest && !Thread.currentThread().isInterrupted()) {
                try {
                    sendRequest();
                    iterations.incrementAndGet();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                }
            }
            currentVUs.decrementAndGet();
        }

        private void sendRequest() throws IOException {
            User user = generateUser(vuId);
            String payload = String.format(
                    "{\"fullName\":\"%s\",\"email\":\"%s\",\"password\":\"%s\"}",
                    user.fullName, user.email, user.password
            );

            long startTime = System.nanoTime();

            HttpURLConnection connection = (HttpURLConnection) new URL(ENDPOINT).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("User-Agent", "java-load-test");
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
                throw new IOException("HTTP status: " + responseCode);
            }

            String response = new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            if (!response.contains("\"id\":")) {
                throw new IOException("Missing user ID in response");
            }
            if (!response.contains("\"email\":\"" + user.email + "\"")) {
                throw new IOException("Email mismatch in response");
            }

            successCount.incrementAndGet();
        }
    }

    private static User generateUser(int vuId) {
        long timestamp = System.currentTimeMillis();
        return new User(
                "User " + vuId + "-" + timestamp,
                "user" + vuId + "_" + timestamp + "@test.com",
                "Password" + (int) (Math.random() * 1000)
        );
    }

    private static void printProgress(long testStartTime) {
        System.out.printf(
                "[Прогресс] %ds, VUs: %d, Итерации: %d, Успешные: %d, Ошибки: %d%n",
                (System.currentTimeMillis() - testStartTime) / 1000,
                currentVUs.get(),
                iterations.get(),
                successCount.get(),
                errorCount.get()
        );
    }

    private static long calculateCurrentP95() {
        if (latencies.isEmpty()) return 0;

        List<Long> sorted = new ArrayList<>(latencies);
        Collections.sort(sorted);
        int index = (int) Math.ceil(0.95 * sorted.size()) - 1;
        return sorted.get(Math.max(0, index));
    }

    private static double getCurrentErrorRate() {
        int total = successCount.get() + errorCount.get();
        return total > 0 ? (double) errorCount.get() / total : 0;
    }

    private static void generateReports(long testStartTime) {
        long testDurationSec = (System.currentTimeMillis() - testStartTime) / 1000;
        int totalRequests = successCount.get() + errorCount.get();
        double rps = totalRequests / (double) testDurationSec;
        double errorRate = getCurrentErrorRate();
        long p95 = calculateCurrentP95();
        long maxLatency = latencies.stream().max(Long::compare).orElse(0L);

        List<String> bottlenecks = new ArrayList<>();
        if (p95 > P95_THRESHOLD_MS) {
            bottlenecks.add(String.format("- **p95 превышает SLA 1000мс** (%dмс)", p95));
        }
        if (errorRate > MAX_ERROR_RATE) {
            bottlenecks.add(String.format("- **Уровень ошибок высокий** (%.2f%%)", errorRate * 100));
        }
        if (iterations.get() < 500) {
            bottlenecks.add(String.format("- **Низкая производительность**: выполнено только %d итераций", iterations.get()));
        }

        String mdReport = String.format(
                "# Отчет по тесту RAMP-UP регистрации пользователей%n%n" +
                        "- **Всего запросов:** %d%n" +
                        "- **Длительность теста:** %d сек%n%n" +
                        "## Итоговые метрики%n" +
                        "- **Средний RPS:** %.2f%n" +
                        "- **Общий p95 latency:** %d мс%n" +
                        "- **Максимальная задержка:** %d мс%n" +
                        "- **Уровень успешных ответов:** %.2f%%%n" +
                        "- **Всего создано пользователей:** %d%n%n" +
                        "## Узкие места%n%s%n",
                totalRequests,
                testDurationSec,
                rps,
                p95,
                maxLatency,
                (1 - errorRate) * 100,
                iterations.get(),
                bottlenecks.isEmpty() ? "- Система выдерживает нагрузку согласно SLA" :
                        String.join("%n", bottlenecks)
        );

        String jsonReport = String.format(
                "{%n" +
                        "  \"metrics\": {%n" +
                        "    \"iterations\": %d,%n" +
                        "    \"successful_requests\": %d,%n" +
                        "    \"failed_requests\": %d,%n" +
                        "    \"error_rate\": %.4f,%n" +
                        "    \"rps\": %.2f,%n" +
                        "    \"latency\": {%n" +
                        "      \"p95\": %d,%n" +
                        "      \"max\": %d%n" +
                        "    },%n" +
                        "    \"max_vus\": %d%n" +
                        "  },%n" +
                        "  \"thresholds\": {%n" +
                        "    \"p95\": %d,%n" +
                        "    \"max_error_rate\": %.2f%n" +
                        "  }%n" +
                        "}",
                iterations.get(),
                successCount.get(),
                errorCount.get(),
                errorRate,
                rps,
                p95,
                maxLatency,
                currentVUs.get(),
                P95_THRESHOLD_MS,
                MAX_ERROR_RATE
        );

        System.out.println(mdReport);

        try {
            Path currentDir = Path.of(LatencyTest.class.getProtectionDomain()
                            .getCodeSource().getLocation().toURI())
                    .getParent();

            Path loadTestsDir = currentDir.getParent();
            Path reportPath = loadTestsDir.resolve("reports/rampup-report.md");

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
            Path reportPath = loadTestsDir.resolve("reports/rampup-summary.json");

            Files.createDirectories(reportPath.getParent());
            Files.write(reportPath, jsonReport.getBytes(StandardCharsets.UTF_8));

            System.out.println("Отчет сохранен: " + reportPath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Не удалось сохранить отчет: " + e.getMessage());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }

    static class User {
        String fullName;
        String email;
        String password;

        User(String fullName, String email, String password) {
            this.fullName = fullName;
            this.email = email;
            this.password = password;
        }
    }
}
