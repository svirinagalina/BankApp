/**
 * Нагрузочный тест для проверки JWT верификации с постепенным увеличением нагрузки.
 *
 * <p><b>Параметры теста:</b>
 * <ul>
 *   <li><b>Количество пользователей:</b> Постепенно увеличивается от 50 до 100 потоков</li>
 *   <li><b>Продолжительность теста:</b> 180 секунд (4 этапа)</li>
 *   <li><b>Этапы нагрузки:</b>
 *     <ol>
 *       <li>50 потоков - 30 секунд</li>
 *       <li>70 потоков - 30 секунд</li>
 *       <li>100 потоков - 60 секунд</li>
 *       <li>100 потоков - 60 секунд</li>
 *     </ol>
 *   </li>
 *   <li><b>Количество тестовых токенов:</b> 10 (по одному на пользователя)</li>
 * </ul>
 *
 * <p><b>Методика тестирования:</b>
 * <ol>
 *   <li>Генерация тестовых JWT токенов с использованием алгоритма HS256</li>
 *   <li>Постепенное увеличение количества параллельных запросов согласно этапам теста</li>
 *   <li>Отправка POST-запросов на эндпоинт верификации (/api/security/verify)</li>
 *   <li>Сбор метрик производительности и ошибок</li>
 * </ol>
 *
 * <p><b>Основные метрики:</b>
 * <ul>
 *   <li><b>Среднее время ответа (avgLatency)</b> - в миллисекундах</li>
 *   <li><b>95-й перцентиль времени ответа (p95)</b> - критический показатель</li>
 *   <li><b>Максимальное время ответа (maxLatency)</b></li>
 *   <li><b>Количество запросов в секунду (RPS)</b></li>
 *   <li><b>Уровень ошибок (errorRate)</b> - процент ошибочных ответов</li>
 * </ul>
 *
 * <p><b>SLA критерии:</b>
 * <ul>
 *   <li>p95 времени ответа не должен превышать 300 мс</li>
 *   <li>Уровень ошибок не должен превышать 1%</li>
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
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class JwtRampUpTest {
    private static final String ENDPOINT = "http://localhost:8085/api/security/verify";
    private static final String SECRET_KEY = "testsecretkeyfortestpurposesonly1234567890";
    private static final List<String> tokens = new ArrayList<>();

    private static final List<StageConfig> STAGES = List.of(
            new StageConfig(30, 50),
            new StageConfig(30, 70),
            new StageConfig(60, 100),
            new StageConfig(60, 100)
    );

    private static final long P95_THRESHOLD_MS = 300;
    private static final double MAX_ERROR_RATE = 0.01;

    private static final List<Long> latencies = new CopyOnWriteArrayList<>();
    private static final AtomicInteger successCount = new AtomicInteger();
    private static final AtomicInteger errorCount = new AtomicInteger();
    private static final AtomicInteger activeThreads = new AtomicInteger();

    public static void main(String[] args) throws Exception {
        generateTestTokens(10);
        TestResults results = runRampUpTest();
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

    private static TestResults runRampUpTest() throws InterruptedException {
        ExecutorService executor = Executors.newCachedThreadPool();
        TestResults results = new TestResults();
        long testStartTime = System.currentTimeMillis();

        for (StageConfig stage : STAGES) {
            long stageEndTime = System.currentTimeMillis() + stage.durationSeconds * 1000;
            int targetThreads = stage.targetThreads;

            while (activeThreads.get() < targetThreads) {
                activeThreads.incrementAndGet();
                executor.submit(() -> {
                    try {
                        runVirtualUser(results);
                    } finally {
                        activeThreads.decrementAndGet();
                    }
                });
            }

            while (System.currentTimeMillis() < stageEndTime) {
                Thread.sleep(1000);
                System.out.printf("Этап: %ds, Активных потоков: %d/%d%n",
                        (System.currentTimeMillis() - testStartTime) / 1000,
                        activeThreads.get(),
                        targetThreads);
            }
        }

        executor.shutdownNow();
        results.testDurationMs = System.currentTimeMillis() - testStartTime;
        return results;
    }

    private static void runVirtualUser(TestResults results) {
        Random random = new Random();

        while (!Thread.currentThread().isInterrupted()) {
            String token = tokens.get(random.nextInt(tokens.size()));
            long startTime = System.nanoTime();

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
            }
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
        double rps = totalRequests / (results.testDurationMs / 1000.0);

        List<String> bottlenecks = new ArrayList<>();
        if (p95 > P95_THRESHOLD_MS) {
            bottlenecks.add(String.format("- **p95 превышает SLA 300мс** (%.2f мс)", (double)p95));
        }
        if (errorRate > MAX_ERROR_RATE) {
            bottlenecks.add(String.format("- **Уровень ошибок высокий** (%.2f%%)", errorRate * 100));
        }

        String mdReport = String.format(
                "# Отчет по тесту RAMP-UP JWT верификации%n%n" +
                        "- **Всего запросов:** %d%n" +
                        "- **Длительность:** %.1f сек%n%n" +
                        "## Итоговые метрики%n" +
                        "- **Средний RPS:** %.2f%n" +
                        "- **Общий p95:** %.2f мс%n" +
                        "- **Макс. задержка:** %d мс%n" +
                        "- **Общий уровень ошибок:** %.2f%%%n%n" +
                        "## Узкие места%n%s%n",
                totalRequests,
                results.testDurationMs / 1000.0,
                rps,
                (double)p95,
                maxLatency,
                errorRate * 100,
                bottlenecks.isEmpty() ? "- Система выдерживает нагрузку согласно SLA" :
                        bottlenecks.stream().collect(Collectors.joining("%n"))
        );

        System.out.println(mdReport);

        try {
            Path currentDir = Path.of(JwtLatencyTest.class.getProtectionDomain()
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
    }

    static class StageConfig {
        final int durationSeconds;
        final int targetThreads;

        StageConfig(int durationSeconds, int targetThreads) {
            this.durationSeconds = durationSeconds;
            this.targetThreads = targetThreads;
        }
    }

    static class TestResults {
        long testDurationMs;
    }
}