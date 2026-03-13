package com.memoria.orchestrator.model;

public record InterventionResponse(
        DiagnosisResponse diagnosis,
        String therapyMode,
        boolean caregiverNotificationSent,
        boolean regionalAudioPrepared
) {
}
