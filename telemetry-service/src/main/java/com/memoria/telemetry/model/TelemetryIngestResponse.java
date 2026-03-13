package com.memoria.telemetry.model;

import java.time.Instant;

public record TelemetryIngestResponse(
        String status,
        String correlationId,
        Instant acceptedAt
) {
}
