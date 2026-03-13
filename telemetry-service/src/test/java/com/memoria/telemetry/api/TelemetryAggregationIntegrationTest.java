package com.memoria.telemetry.api;

import com.memoria.telemetry.aggregation.TelemetryAggregationWorker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = {
    "spring.kafka.listener.auto-startup=false",
    "spring.kafka.admin.auto-create=false"
})
@Testcontainers(disabledWithoutDocker = true)
class TelemetryAggregationIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("pgvector/pgvector:pg16"))
            .withDatabaseName("memoria")
            .withUsername("memoria")
            .withPassword("memoria");

    @DynamicPropertySource
    static void dbProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> true);
    }

    @Autowired
    private TelemetryAggregationWorker worker;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldAggregateAndFlush() {
        worker.onTelemetry("{\"patientId\":\"p-test\",\"timestamp\":\"2026-03-13T10:00:00Z\",\"events\":[{\"metricType\":\"accel\",\"value\":4.8}]}");
        worker.flushFiveMinuteWindows();

        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM telemetry_aggregates WHERE patient_id='p-test'", Integer.class);
        assertTrue(count != null && count > 0);
    }
}
