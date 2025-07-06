/**
 * THROUGHPUT тест для проверки пропускной способности JWT верификации.
 *
 * <p><b>Параметры теста:</b>
 * <ul>
 *   <li><b>Целевой RPS:</b> 200 запросов в секунду</li>
 *   <li><b>Продолжительность теста:</b> 120 секунд</li>
 *   <li><b>Максимальное количество виртуальных пользователей:</b> 1000</li>
 *   <li><b>Предварительно выделенные пользователи:</b> 100</li>
 *   <li><b>Количество тестовых токенов:</b> 10</li>
 * </ul>
 *
 * <p><b>Методика тестирования:</b>
 * <ol>
 *   <li>Генерация тестовых JWT токенов с использованием алгоритма HS256</li>
 *   <li>Автоматическое масштабирование количества активных запросов для достижения целевого RPS</li>
 *   <li>Отправка POST-запросов на эндпоинт верификации (/api/security/verify)</li>
 *   <li>Мониторинг активных запросов и адаптивная регулировка нагрузки</li>
 *   <li>Сбор метрик производительности и ошибок</li>
 * </ol>
 *
 * <p><b>Основные метрики:</b>
 * <ul>
 *   <li><b>Фактический RPS</b> - достигнутое количество запросов в секунду</li>
 *   <li><b>Среднее время ответа</b> - в миллисекундах</li>
 *   <li><b>95-й перцентиль времени ответа (p95)</b></li>
 *   <li><b>Максимальное время ответа</b></li>
 *   <li><b>Уровень ошибок</b> - процент ошибочных ответов</li>
 *   <li><b>Количество активных запросов</b> - пиковое значение</li>
 * </ul>
 *
 * <p><b>SLA критерии:</b>
 * <ul>
 *   <li>p95 времени ответа не должен превышать 500 мс</li>
 *   <li>Уровень ошибок не должен превышать 1%</li>
 * </ul>
 *
 * <p><b>Особенности реализации:</b>
 * <ul>
 *   <li>Использует пул из 1000 потоков для максимальной нагрузки</li>
 *   <li>Адаптивно увеличивает количество активных запросов каждые 10 секунд</li>
 *   <li>Контролирует частоту запросов через отдельный планировщик</li>
 *   <li>Собирает статистику в многопоточной среде (CopyOnWriteArrayList)</li>
 * </ul>
 */
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
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
import java.util.stream.Collectors;

public class JwtThroughputTest {
    private static final String ENDPOINT = "http://localhost:8085/api/security/verify";
    private static final String SECRET_KEY = "testsecretkeyfortestpurposesonly1234567890";
    private static final List<String> tokens = new ArrayList<>();

    private static final int TARGET_RPS = 200;
    private static final int TEST_DURATION_SEC = 120;
    private static final int MAX_VIRTUAL_USERS = 1000;
    private static final int PRE_ALLOCATED_VUS = 100;

    private static final long P95_THRESHOLD_MS = 500;
    private static final double MAX_ERROR_RATE = 0.01;

    private static final List<Long> latencies = new CopyOnWriteArrayList<>();
    private static final AtomicInteger successCount = new AtomicInteger();
    private static final AtomicInteger errorCount = new AtomicInteger();
    private static final AtomicInteger activeRequests = new AtomicInteger();

    public static void main(String[] args) throws Exception {
        generateTestTokens(10);
        TestResults results = runThroughputTest();
        generateReport(results);
    }

    private static void generateTestTokens(int count) throws Exception {
        for (int i = 0; i < count; i++) {
            tokens.add(generateToken("user" + i));
        }
    }

    private static String generateToken(String userId) throws Exception {
        String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        long now = System.currentTimeMillis() / 1000;
        String payload = String.format(
                "{\"sub\":\"%s\",\"iat\":%d,\"exp\":%d}",
                userId, now, now + 3600
        );

        String encodedHeader = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(header.getBytes(StandardCharsets.UTF_8));
        String encodedPayload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(payload.getBytes(StandardCharsets.UTF_8));

        String signature = hmacSha256(encodedHeader + "." + encodedPayload, SECRET_KEY);

        return encodedHeader + "." + encodedPayload + "." + signature;
    }

    private static String hmacSha256(String data, String secret) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKey);
        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
    }

    private static TestResults runThroughputTest() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(MAX_VIRTUAL_USERS);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        TestResults results = new TestResults();
        long testStartTime = System.currentTimeMillis();

        for (int i = 0; i < PRE_ALLOCATED_VUS; i++) {
            executor.submit(() -> runVirtualUser(results));
        }

        scheduler.scheduleAtFixedRate(() -> {
            int currentVus = activeRequests.get();
            int requiredVus = Math.min(MAX_VIRTUAL_USERS, currentVus * 2);

            if (currentVus < requiredVus) {
                int toAdd = requiredVus - currentVus;
                for (int i = 0; i < toAdd; i++) {
                    executor.submit(() -> runVirtualUser(results));
                }
            }
        }, 10, 10, TimeUnit.SECONDS);

        ScheduledExecutorService rpsController = Executors.newScheduledThreadPool(1);
        rpsController.scheduleAtFixedRate(() -> {
            try {
                int requestsToSend = TARGET_RPS;
                while (requestsToSend-- > 0) {
                    executor.submit(() -> sendRequest(results));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.SECONDS);

        Thread.sleep(TEST_DURATION_SEC * 1000);

        scheduler.shutdownNow();
        rpsController.shutdownNow();
        executor.shutdownNow();

        results.testDurationMs = System.currentTimeMillis() - testStartTime;
        return results;
    }

    private static void runVirtualUser(TestResults results) {
        Random random = new Random();
        while (!Thread.currentThread().isInterrupted()) {
            try {
                sendRequest(results);
                Thread.sleep(random.nextInt(100));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static void sendRequest(TestResults results) {
        String token = tokens.get(ThreadLocalRandom.current().nextInt(tokens.size()));
        long startTime = System.nanoTime();
        activeRequests.incrementAndGet();

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(ENDPOINT).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(token.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = connection.getResponseCode();
            long latencyMs = (System.nanoTime() - startTime) / 1_000_000;

            synchronized (latencies) {
                latencies.add(latencyMs);
            }

            if (responseCode == 200) {
                successCount.incrementAndGet();
            } else {
                errorCount.incrementAndGet();
            }
        } catch (IOException e) {
            errorCount.incrementAndGet();
        } finally {
            activeRequests.decrementAndGet();
        }
    }

    private static void generateReport(TestResults results) {
        if (latencies.isEmpty()) {
            System.out.println("No data for report");
            return;
        }

        Collections.sort(latencies);

        double avgLatency = latencies.stream().mapToLong(Long::longValue).average().orElse(0);
        long p95 = latencies.get((int) (latencies.size() * 0.95));
        long maxLatency = latencies.get(latencies.size() - 1);
        int totalRequests = successCount.get() + errorCount.get();
        double errorRate = totalRequests > 0 ? (double) errorCount.get() / totalRequests : 0;
        double actualRps = totalRequests / (results.testDurationMs / 1000.0);

        List<String> bottlenecks = detectBottlenecks(p95, maxLatency, errorRate);

        String mdReport = String.format(
                "# Отчёт по нагрузочному тестированию JWT\n\n" +
                        "## Основные метрики\n" +
                        "| Параметр               | Значение          |\n" +
                        "|------------------------|-------------------|\n" +
                        "| Длительность теста     | %.1f сек          |\n" +
                        "| Всего запросов         | %d                |\n" +
                        "| RPS (факт/цель)       | %.2f/%d           |\n" +
                        "| Среднее время ответа  | %.2f мс           |\n" +
                        "| p95 время ответа      | %.2f мс           |\n" +
                        "| Максимальная задержка| %d мс             |\n" +
                        "| Ошибки                | %d (%.2f%%)       |\n\n" +
                        "## Узкие места\n%s\n",
                results.testDurationMs / 1000.0,
                totalRequests,
                actualRps, TARGET_RPS,
                avgLatency,
                (double)p95,
                maxLatency,
                errorCount.get(), errorRate * 100,
                bottlenecks.stream().collect(Collectors.joining("\n"))
        );

        System.out.println(mdReport);

        try {
            Path currentDir = Path.of(JwtLatencyTest.class.getProtectionDomain()
                            .getCodeSource().getLocation().toURI())
                    .getParent();

            Path loadTestsDir = currentDir.getParent();

            Path reportPath = loadTestsDir.resolve("reports/jwt-throughput-report.md");

            Files.createDirectories(reportPath.getParent());

            Files.write(reportPath, mdReport.getBytes(StandardCharsets.UTF_8));

            System.out.println("Отчет сохранен: " + reportPath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении отчетов: " + e.getMessage());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String> detectBottlenecks(long p95, long maxLatency, double errorRate) {
        List<String> bottlenecks = new ArrayList<>();

        if (p95 > 50) {
            bottlenecks.add(String.format("- Высокий p95 (%.2f мс): 5%% запросов обрабатываются дольше 50 мс", (double)p95));
        }

        if (maxLatency > 300) {
            bottlenecks.add(String.format("- Выбросы задержки (до %d мс): Есть отдельные медленные запросы", maxLatency));
        }

        if (errorRate > 0) {
            bottlenecks.add(String.format("- Ошибки (%d): Проверьте логи сервера", errorCount.get()));
        }

        if (bottlenecks.isEmpty()) {
            bottlenecks.add("- Явных узких мест не обнаружено");
        }

        return bottlenecks;
    }

    private static String generateJsonReport(TestResults results, long p95, double avgLatency,
                                             long maxLatency, int totalRequests,
                                             double errorRate, double actualRps) {
        return String.format(
                "{\n" +
                        "  \"metrics\": {\n" +
                        "    \"duration_sec\": %.1f,\n" +
                        "    \"total_requests\": %d,\n" +
                        "    \"target_rps\": %d,\n" +
                        "    \"actual_rps\": %.2f,\n" +
                        "    \"response_time\": {\n" +
                        "      \"avg\": %.2f,\n" +
                        "      \"p95\": %.2f,\n" +
                        "      \"max\": %d\n" +
                        "    },\n" +
                        "    \"errors\": {\n" +
                        "      \"count\": %d,\n" +
                        "      \"rate\": %.4f\n" +
                        "    }\n" +
                        "  },\n" +
                        "  \"thresholds\": {\n" +
                        "    \"p95\": %d,\n" +
                        "    \"max_error_rate\": %.2f\n" +
                        "  }\n" +
                        "}",
                results.testDurationMs / 1000.0,
                totalRequests,
                TARGET_RPS,
                actualRps,
                avgLatency,
                (double)p95,
                maxLatency,
                errorCount.get(),
                errorRate,
                P95_THRESHOLD_MS,
                MAX_ERROR_RATE
        );
    }

    static class TestResults {
        long testDurationMs;
    }
}
