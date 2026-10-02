package com.rbdip.bookstore.migration;

import static org.assertj.core.api.Assertions.assertThat;

import com.rbdip.bookstore.order.CreateOrderRequest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.flyway.enabled=false")
class ZeroDowntimeMigrationLoadTest {
    private static final int CLIENTS = 4;
    private static final int WARMUP_REQUESTS = 40;
    private static final int MIGRATION_REQUESTS = 40;
    private static final long TIMEOUT_SECONDS = 30;

    private static final int SCHEMA_VERSION_BEFORE_CONTRACT = 6;

    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private TestRestTemplate restTemplate;

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        postgres.start();

        MigrationInfo[] pending = flyway().info().pending();
        System.out.println("PENDING: " + Arrays.toString(pending));
        String lastBeforeContract = pending[SCHEMA_VERSION_BEFORE_CONTRACT - 1].getVersion().getVersion();
        Flyway.configure().dataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())
                .target(lastBeforeContract).load().migrate();

        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    private static Flyway flyway() {
        return Flyway.configure()
                .dataSource(
                        postgres.getJdbcUrl(),
                        postgres.getUsername(),
                        postgres.getPassword())
                .load();
    }

    @Test
    void apiServesRequestsWithoutErrorsWhileContractMigrationRuns()
            throws Exception {

        Long productId = createProduct();

        CreateOrderRequest order = new CreateOrderRequest(
                "Ivan Petrov",
                "Moscow, Lenina 1",
                null,
                "regular",
                null,
                List.of(
                        new CreateOrderRequest.Item(productId, 1)));

        List<String> failures = new CopyOnWriteArrayList<>();

        AtomicInteger requests = new AtomicInteger();
        AtomicBoolean running = new AtomicBoolean(true);

        ExecutorService pool = Executors.newFixedThreadPool(CLIENTS);

        for (int i = 0; i < CLIENTS; i++) {
            pool.submit(() -> {
                while (running.get()) {
                    try {
                        check(
                                restTemplate.postForEntity(
                                        "/orders",
                                        order,
                                        Map.class),
                                failures);

                        check(
                                restTemplate.getForEntity(
                                        "/orders",
                                        List.class),
                                failures);

                        requests.addAndGet(2);
                    } catch (RuntimeException e) {
                        failures.add(e.toString());
                    }
                }
            });
        }

        awaitRequests(
                requests,
                WARMUP_REQUESTS);

        int requestsBeforeMigration = requests.get();

        flyway().migrate();

        awaitRequests(
                requests,
                requestsBeforeMigration + MIGRATION_REQUESTS);

        running.set(false);

        pool.shutdown();

        assertThat(pool.awaitTermination(
                TIMEOUT_SECONDS,
                TimeUnit.SECONDS))
                .isTrue();

        assertThat(flyway().info().pending())
                .isEmpty();

        assertThat(failures)
                .isEmpty();
    }

    private Long createProduct() {
        ResponseEntity<Map> response =
                restTemplate.postForEntity(
                        "/products",
                        Map.of(
                                "name", "Refactoring Databases",
                                "price", new BigDecimal("45.00")),
                        Map.class);

        assertThat(response.getStatusCode().is2xxSuccessful())
                .isTrue();

        return Long.valueOf(
                response.getBody()
                        .get("id")
                        .toString());
    }

    private static void check(
            ResponseEntity<?> response,
            List<String> failures) {

        if (!response.getStatusCode().is2xxSuccessful()) {
            failures.add(
                    response.getStatusCode()
                            + " "
                            + response.getBody());
        }
    }

    private static void awaitRequests(
            AtomicInteger requests,
            int target)
            throws InterruptedException {

        long deadline =
                System.nanoTime()
                        + TimeUnit.SECONDS.toNanos(TIMEOUT_SECONDS);

        while (requests.get() < target) {
            assertThat(System.nanoTime())
                    .as(
                            "load did not reach %d requests",
                            target)
                    .isLessThan(deadline);

            TimeUnit.MILLISECONDS.sleep(10);
        }
    }
}
