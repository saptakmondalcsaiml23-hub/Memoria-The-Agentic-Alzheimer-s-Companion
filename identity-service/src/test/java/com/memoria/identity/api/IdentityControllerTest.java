package com.memoria.identity.api;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Requires JWT issuer and Postgres in CI runtime")
class IdentityControllerTest {

    @Test
    void contextLoads() {
        // Placeholder integration smoke test.
    }
}
