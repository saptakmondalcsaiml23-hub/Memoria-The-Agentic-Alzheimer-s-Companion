package com.memoria.orchestrator.model;

import jakarta.validation.constraints.NotBlank;

public record DiagnosisRequest(
        @NotBlank String patientId,
        @NotBlank String telemetryNarrative
) {
}
