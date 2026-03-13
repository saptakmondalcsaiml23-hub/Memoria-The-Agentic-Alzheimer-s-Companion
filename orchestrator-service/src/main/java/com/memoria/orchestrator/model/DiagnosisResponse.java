package com.memoria.orchestrator.model;

public record DiagnosisResponse(
        int anomalyScore,
        String condition,
        String action,
        String urgency,
        boolean caregiverNotify
) {
}
