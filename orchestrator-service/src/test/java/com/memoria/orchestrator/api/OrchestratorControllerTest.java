package com.memoria.orchestrator.api;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Requires external provider endpoints and Postgres in CI runtime")
class OrchestratorControllerTest {

    @Test
    void contextLoads() {
        // Placeholder integration smoke test.
    }
}
