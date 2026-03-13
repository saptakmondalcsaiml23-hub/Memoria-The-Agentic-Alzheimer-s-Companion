package com.memoria.orchestrator.model;

public record ClinicalVisionEvalResponse(
        String patientId,
        String assessment,
        int score,
        String note
) {
}
