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
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class JwtSoakTest {
    private static final String ENDPOINT = "http://localhost:8085/api/security/verify";
    private static final String SECRET_KEY = "testsecretkeyfortestpurposesonly1234567890";
    private static final List<String> TOKENS = generateTokens(10);

    // Параметры теста
    private static final int VIRTUAL_USERS = 50;
    private static final int DURATION_MINUTES = 30;
    private static final int EXPECTED_RPS = 120;

    // SLA параметры
    private static final long P95_THRESHOLD_MS = 400;
    private static final double MAX_ERROR_RATE = 0.001;
    private static final long RESPONSE_TIMEOUT_MS = 10_000;

    // Метрики
    private static final List<Long> latencies = new CopyOnWriteArrayList<>();
    private static final AtomicInteger successCount = new AtomicInteger();
    private static final AtomicInteger errorCount = new AtomicInteger();
    private static final AtomicInteger failedChecks = new AtomicInteger();
    private static final AtomicInteger passedChecks = new AtomicInteger();
    private static final AtomicLong lastThirdStartTime = new AtomicLong();

    public static void main(String[] args) throws Exception {
        runSoakTest();
    }

    private static List<String> generateTokens(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> {
                    try {
                        return generateToken("user" + i);
                    } catch (Exception e) {
                        throw new RuntimeException("Token generation failed", e);
                    }
                })
                .collect(Collectors.toList());
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

    private static void runSoakTest() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(VIRTUAL_USERS);
        ScheduledExecutorService statsCollector = Executors.newSingleThreadScheduledExecutor();
        long testStartTime = System.currentTimeMillis();
        lastThirdStartTime.set(testStartTime + (DURATION_MINUTES * 60_000 * 2 / 3));

        // Запускаем сбор статистики каждые 30 секунд
        statsCollector.scheduleAtFixedRate(() -> {
            System.out.printf("[Progress] %d/%d minutes, Requests: %d, Errors: %d%n",
                    (System.currentTimeMillis() - testStartTime) / 60_000,
                    DURATION_MINUTES,
                    successCount.get() + errorCount.get(),
                    errorCount.get());
        }, 1, 1, TimeUnit.MINUTES);

        // Запускаем виртуальных пользователей
        for (int i = 0; i < VIRTUAL_USERS; i++) {
            executor.submit(() -> {
                Random random = new Random();
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        sendRequest(i % TOKENS.size());
                        Thread.sleep(random.nextInt(1000)); // Имитация поведения пользователя
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }

        // Ждем завершения теста
        Thread.sleep(DURATION_MINUTES * 60_000);

        // Останавливаем все
        executor.shutdownNow();
        statsCollector.shutdownNow();

        // Генерируем отчет
        generateReport(testStartTime);
    }

    private static void sendRequest(int tokenIndex) {
        String token = TOKENS.get(tokenIndex);
        long startTime = System.nanoTime();

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(ENDPOINT).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setConnectTimeout((int) RESPONSE_TIMEOUT_MS);
            connection.setReadTimeout((int) RESPONSE_TIMEOUT_MS);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(token.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = connection.getResponseCode();
            long latencyMs = (System.nanoTime() - startTime) / 1_000_000;

            synchronized (latencies) {
                latencies.add(latencyMs);
            }

            // Проверки (checks)
            if (responseCode == 200) {
                passedChecks.incrementAndGet();
            } else {
                failedChecks.incrementAndGet();
            }

            if (latencyMs < 500) {
                passedChecks.incrementAndGet();
            } else {
                failedChecks.incrementAndGet();
            }

            if (responseCode == 200) {
                successCount.incrementAndGet();
            } else {
                errorCount.incrementAndGet();
            }
        } catch (IOException e) {
            errorCount.incrementAndGet();
            failedChecks.incrementAndGet();
        }
    }

    private static void generateReport(long testStartTime) {
        if (latencies.isEmpty()) {
            System.out.println("No data for report");
            return;
        }

        Collections.sort(latencies);

        // Общие метрики
        long testDurationMs = System.currentTimeMillis() - testStartTime;
        int totalRequests = successCount.get() + errorCount.get();
        double actualRps = totalRequests / (testDurationMs / 1000.0);
        double errorRate = totalRequests > 0 ? (double) errorCount.get() / totalRequests : 0;
        int totalChecks = passedChecks.get() + failedChecks.get();
        double failedChecksRate = totalChecks > 0 ? (double) failedChecks.get() / totalChecks * 100 : 0;

        // Расчет перцентилей
        double avgLatency = latencies.stream().mapToLong(Long::longValue).average().orElse(0);
        long p95 = latencies.get((int) (latencies.size() * 0.95));
        long p90 = latencies.get((int) (latencies.size() * 0.90));
        long p99 = latencies.get((int) (latencies.size() * 0.99));
        long maxLatency = latencies.get(latencies.size() - 1);

        // Анализ деградации производительности (последняя треть теста)
        List<Long> lastThirdLatencies = latencies.stream()
                .filter(l -> System.currentTimeMillis() - testStartTime > lastThirdStartTime.get())
                .collect(Collectors.toList());

        long lastThirdP95 = !lastThirdLatencies.isEmpty() ?
                lastThirdLatencies.get((int) (lastThirdLatencies.size() * 0.95)) : 0;
        double perfDegradation = !lastThirdLatencies.isEmpty() ?
                ((double) lastThirdP95 / p95 - 1) * 100 : 0;

        // Проверка SLA
        List<String> slaChecks = new ArrayList<>();
        if (p95 <= P95_THRESHOLD_MS) {
            slaChecks.add(String.format("**p95 latency в пределах SLA** (%.2f ms)", (double) p95));
        } else {
            slaChecks.add(String.format("**p95 latency превышает SLA 400ms** (%.2f ms)", (double) p95));
        }

        if (errorRate <= MAX_ERROR_RATE) {
            slaChecks.add(String.format("**Уровень ошибок в пределах нормы** (%.4f%%)", errorRate * 100));
        } else {
            slaChecks.add(String.format("**Уровень ошибок превышает 0.1%%** (%.4f%%)", errorRate * 100));
        }

        // Выявление узких мест
        List<String> bottlenecks = new ArrayList<>();
        if (perfDegradation > 20) {
            bottlenecks.add(String.format("- **Деградация производительности**: p95 вырос на %.2f%% за время теста", perfDegradation));
        }

        if (errorRate > 0 && errorRate < MAX_ERROR_RATE) {
            bottlenecks.add(String.format("- **Накопление ошибок**: обнаружены спорадические ошибки (%d всего)", errorCount.get()));
        }

        if (actualRps < EXPECTED_RPS * 0.9) {
            bottlenecks.add(String.format("- **Низкая пропускная способность**: RPS %.2f при целевом %d", actualRps, EXPECTED_RPS));
        }

        if (bottlenecks.isEmpty()) {
            bottlenecks.add("- Критических узких мест не обнаружено");
        }

        // Формирование отчета
        String mdReport = String.format(
                "# Отчет SOAK-теста JWT верификации%n%n" +
                        "## Основные параметры теста%n" +
                        "| Параметр               | Значение          |%n" +
                        "|------------------------|------------------|%n" +
                        "| Длительность          | %d минут         |%n" +
                        "| Виртуальных пользователей | %d VU         |%n" +
                        "| Всего запросов        | %d |%n" +
                        "| Средний RPS           | %.2f |%n" +
                        "| Проверок (checks)     | %d (%d failed)%n%n" +
                        "## Ключевые метрики производительности%n" +
                        "- **p90 latency:** %.2f ms%n" +
                        "- **p95 latency:** %.2f ms%n" +
                        "- **p99 latency:** %.2f ms%n" +
                        "- **Максимальная задержка:** %d ms%n" +
                        "- **Уровень ошибок:** %.4f%%%n" +
                        "- **Пропускная способность:** %.2f запр/сек%n" +
                        "- **Ошибки проверок:** %.2f%%%n%n" +
                        "## Проверка SLA%n%s%n%n" +
                        "## Узкие места (Bottlenecks)%n%s%n",
                DURATION_MINUTES,
                VIRTUAL_USERS,
                totalRequests,
                actualRps,
                totalChecks, failedChecks.get(),
                (double) p90,
                (double) p95,
                (double) p99,
                maxLatency,
                errorRate * 100,
                actualRps,
                failedChecksRate,
                String.join("%n", slaChecks),
                String.join("%n", bottlenecks)
        );

        System.out.println(mdReport);

        try {
            Path currentDir = Path.of(JwtLatencyTest.class.getProtectionDomain()
                            .getCodeSource().getLocation().toURI())
                    .getParent();

            Path loadTestsDir = currentDir.getParent();

            Path reportPath = loadTestsDir.resolve("reports/soak-report.md");

            Files.createDirectories(reportPath.getParent());

            Files.write(reportPath, mdReport.getBytes(StandardCharsets.UTF_8));

            System.out.println("Отчет сохранен: " + reportPath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении отчетов: " + e.getMessage());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}