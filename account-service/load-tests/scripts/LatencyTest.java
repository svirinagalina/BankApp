import java.util.*;
import java.util.concurrent.*;
import java.net.*;
import java.io.*;

public class LatencyTest {
    private static final String BASE_URL = "http://localhost:8084/api/users/register";
    private static final int WARMUP_ITERATIONS = 100;
    private static final int TEST_ITERATIONS = 1000;
    private static final int THREAD_COUNT = 50;
    private static final int SLA_THRESHOLD_MS = 500;

    public static void main(String[] args) throws Exception {
        System.out.println("Starting warm-up phase...");
        warmUp();

        System.out.println("Starting load test phase...");
        List<Long> latencies = runLoadTest();

        analyzeResults(latencies);
    }

    private static void warmUp() throws Exception {
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            sendRequest(i);
        }
    }

    private static List<Long> runLoadTest() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future<Long>> futures = new ArrayList<>();

        for (int i = 0; i < TEST_ITERATIONS; i++) {
            final int requestNum = i;
            futures.add(executor.submit(() -> sendRequest(requestNum)));
        }

        List<Long> latencies = new ArrayList<>();
        for (Future<Long> future : futures) {
            latencies.add(future.get());
        }

        executor.shutdown();
        return latencies;
    }

    private static long sendRequest(int requestNum) throws IOException {
        long startTime = System.currentTimeMillis();

        String email = "user" + requestNum + System.currentTimeMillis() + "@test.com";
        String requestBody = String.format(
                "{\"fullName\":\"Test User\", \"email\":\"%s\", \"password\":\"password123\"}",
                email
        );

        HttpURLConnection connection = (HttpURLConnection) new URL(BASE_URL).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            os.write(requestBody.getBytes());
        }

        int responseCode = connection.getResponseCode();
        if (responseCode != 201) {
            System.err.println("Error in request #" + requestNum + ": " + responseCode);
        }

        return System.currentTimeMillis() - startTime;
    }

    private static void analyzeResults(List<Long> latencies) {
        Collections.sort(latencies);

        long avg = latencies.stream().mapToLong(Long::longValue).sum() / latencies.size();
        long p95 = latencies.get((int) (latencies.size() * 0.95));
        long p99 = latencies.get((int) (latencies.size() * 0.99));
        long max = latencies.get(latencies.size() - 1);

        System.out.println("\n=== PERFORMANCE REPORT ===");
        System.out.println("Total requests: " + latencies.size());
        System.out.println("Average latency: " + avg + " ms");
        System.out.println("95th percentile: " + p95 + " ms");
        System.out.println("99th percentile: " + p99 + " ms");
        System.out.println("Max latency: " + max + " ms");

        if (p95 > SLA_THRESHOLD_MS) {
            System.out.println("\n WARNING: 95th percentile exceeds SLA threshold (" + SLA_THRESHOLD_MS + " ms)");
        } else {
            System.out.println("\n SLA compliant: 95th percentile within acceptable range");
        }

        if (max > avg * 3) {
            System.out.println(" BOTTLENECK DETECTED: Significant variance between average and max latency");
        }
    }
}