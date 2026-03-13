package com.memoria.telemetry.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TelemetryEvent(
        @NotBlank String metricType,
        @NotNull Double value
) {
}
