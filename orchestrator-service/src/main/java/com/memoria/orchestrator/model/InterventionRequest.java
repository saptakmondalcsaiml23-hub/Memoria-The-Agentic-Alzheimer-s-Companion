package com.memoria.orchestrator.model;

import jakarta.validation.constraints.NotBlank;

public record InterventionRequest(
        @NotBlank String patientId,
        @NotBlank String caregiverPhone,
        @NotBlank String narrative,
        String preferredLanguage
) {
}
