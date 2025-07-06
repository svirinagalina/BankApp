/**
 * Нагрузочный тест для измерения времени верификации JWT-токенов.
 *
 * <h3>Методика тестирования:</h3>
 * - Отправка 500 последовательных запросов на endpoint верификации
 * - Замер времени отклика для каждого запроса
 * - Анализ перцентилей задержки
 *
 * <h3>Параметры теста:</h3>
 * <ul>
 *   <li>URL: http://localhost:8085/api/security/verify</li>
 *   <li>Метод: POST</li>
 *   <li>Виртуальных пользователей: 1</li>
 *   <li>Всего запросов: 500</li>
 *   <li>Тип нагрузки: постоянная (без увеличения)</li>
 * </ul>
 *
 * <h3>Ключевые метрики:</h3>
 * <ul>
 *   <li>Средняя задержка (avg)</li>
 *   <li>95-й перцентиль задержки (p95)</li>
 *   <li>Максимальная задержка (max)</li>
 *   <li>Уровень ошибок (error rate)</li>
 * </ul>
 *
 * <h3>Критерии успеха:</h3>
 * - p95 < 50 мс
 * - Ошибок: 0%
 */
import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.nio.charset.StandardCharsets;

public class JwtLatencyTest {
    private static final String TEST_URL = "http://localhost:8085/api/security/verify";
    private static final String SECRET_KEY = "testsecretkeyfortestpurposesonly1234567890";
    private static final int TOTAL_REQUESTS = 500;
    private static final int THREAD_COUNT = 1;
    private static final long SLA_THRESHOLD_MS = 50;
    private static final long MAX_LATENCY_THRESHOLD_MS = 500;

    private final List<String> tokens = new ArrayList<>();
    private final List<Long> latencies = new CopyOnWriteArrayList<>();
    private final AtomicInteger successCount = new AtomicInteger();
    private final AtomicInteger errorCount = new AtomicInteger();

    public static void main(String[] args) throws Exception {
        JwtLatencyTest test = new JwtLatencyTest();
        test.prepareTestData();
        test.runLoadTest();
        test.generateReport();
    }

    private void prepareTestData() throws Exception {
        for (int i = 0; i < 10; i++) {
            tokens.add(generateToken("user" + i));
        }
    }

    private String generateToken(String userId) throws Exception {
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

    private String hmacSha256(String data, String secret) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKey);
        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
    }

    private void runWarmup() throws Exception {
        System.out.println("Running warmup...");
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future<?>> futures = new ArrayList<>();

        for (Future<?> future : futures) {
            future.get();
        }
        executor.shutdown();
    }

    private void runLoadTest() throws Exception {
        System.out.println("Running load test...");
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future<?>> futures = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < TOTAL_REQUESTS; i++) {
            final int index = i % tokens.size();
            futures.add(executor.submit(() -> sendRequest(tokens.get(index))));
        }

        for (Future<?> future : futures) {
            future.get();
        }

        long testDuration = System.currentTimeMillis() - startTime;
        executor.shutdown();

        System.out.println("Test completed in " + testDuration + " ms");
    }

    private void sendRequest(String token) {
        long startTime = System.nanoTime();

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(TEST_URL).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(token.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = connection.getResponseCode();
            long latency = (System.nanoTime() - startTime) / 1_000_000;

            latencies.add(latency);

            if (responseCode == 200) {
                successCount.incrementAndGet();
            } else {
                errorCount.incrementAndGet();
                System.err.println("Error in request: HTTP " + responseCode);
            }
        } catch (Exception e) {
            errorCount.incrementAndGet();
            System.err.println("Request failed: " + e.getMessage());
        }
    }

    private void generateReport() {
        Collections.sort(latencies);

        double avg = latencies.stream().mapToLong(Long::longValue).average().orElse(0);
        long max = latencies.stream().mapToLong(Long::longValue).max().orElse(0);
        long p90 = getPercentile(90);
        long p95 = getPercentile(95);
        long p99 = getPercentile(99);

        double errorRate = (double) errorCount.get() / TOTAL_REQUESTS * 100;

        List<String> bottlenecks = new ArrayList<>();
        if (avg > SLA_THRESHOLD_MS) bottlenecks.add("- Средняя задержка превышает SLA-порог (" + SLA_THRESHOLD_MS + "ms)");
        if (max > MAX_LATENCY_THRESHOLD_MS) bottlenecks.add("- Обнаружены выбросы высокой задержки (> " + MAX_LATENCY_THRESHOLD_MS + "ms)");
        if (errorCount.get() > 0) bottlenecks.add("- Found errors: " + errorCount.get());

        String report = "# Отчет по тестированию JWT верификации\n\n" +
                "## Results\n" +
                "| Metric                | Value          |\n" +
                "|-----------------------|----------------|\n" +
                "| Средняя задержка      | " + String.format("%.2f", avg) + " ms |\n" +
                "| Максимальная задержка | " + max + " ms |\n" +
                "| 90-й перцентиль       | " + p90 + " ms |\n" +
                "| 95-й перцентиль       | " + p95 + " ms |\n" +
                "| 99-й перцентиль       | " + p99 + " ms |\n" +
                "| Успешные запросы      | " + successCount.get() + "/" + TOTAL_REQUESTS + " |\n" +
                "| Уровень ошибок        | " + String.format("%.2f", errorRate) + "% |\n\n" +
                "## Bottlenecks\n" +
                (bottlenecks.isEmpty() ?
                        "- Система работает стабильно, узких мест не обнаружено" :
                        String.join("\n", bottlenecks));

        System.out.println(report);

        try {
            Path currentDir = Path.of(JwtLatencyTest.class.getProtectionDomain()
                            .getCodeSource().getLocation().toURI())
                    .getParent();

            Path loadTestsDir = currentDir.getParent();

            Path reportPath = loadTestsDir.resolve("reports/latency-report.md");

            Files.createDirectories(reportPath.getParent());

            Files.write(reportPath, report.getBytes(StandardCharsets.UTF_8));

            System.out.println("Отчет сохранен: " + reportPath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Не удалось сохранить отчет: " + e.getMessage());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private long getPercentile(int percentile) {
        if (latencies.isEmpty()) return 0;
        int index = (int) Math.ceil(percentile / 100.0 * latencies.size());
        return latencies.get(Math.min(index, latencies.size() - 1));
    }
}