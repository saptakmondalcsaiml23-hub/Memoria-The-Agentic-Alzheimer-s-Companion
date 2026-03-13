package com.memoria.telemetry.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;

public record TelemetryIngestRequest(
        @NotBlank String patientId,
        @NotNull Instant timestamp,
        @NotEmpty List<@Valid TelemetryEvent> events
) {
}
