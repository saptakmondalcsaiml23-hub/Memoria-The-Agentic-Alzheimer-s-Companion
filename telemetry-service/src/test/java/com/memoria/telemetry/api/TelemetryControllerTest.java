package com.memoria.telemetry.api;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Requires Docker-backed Kafka and Postgres in CI runtime")
class TelemetryControllerTest {

  @Test
  void contextLoads() {
    // Placeholder integration smoke test.
    }
}
