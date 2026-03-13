package com.memoria.orchestrator.api;

import com.memoria.orchestrator.service.RagRetrievalService;
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

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class RagRetrievalIntegrationTest {

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
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RagRetrievalService retrievalService;

    @Test
    void shouldReturnSimilarDays() {
        jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS vector");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS daily_evaluations (id BIGSERIAL PRIMARY KEY, patient_id VARCHAR(64), eval_date DATE, summary_text TEXT, embedding_vector vector(4))");

        jdbcTemplate.update("INSERT INTO daily_evaluations(patient_id, eval_date, summary_text, embedding_vector) VALUES (?, ?, ?, CAST(? AS vector))",
                "p1", LocalDate.now(), "calm day", "[0.1,0.2,0.3,0.4]");

        List<String> out = retrievalService.topSimilarDays("p1", "[0.1,0.2,0.3,0.4]", 1);
        assertFalse(out.isEmpty());
    }
}
