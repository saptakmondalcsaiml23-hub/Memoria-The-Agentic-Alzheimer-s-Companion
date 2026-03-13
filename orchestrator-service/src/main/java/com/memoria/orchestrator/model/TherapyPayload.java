package com.memoria.orchestrator.model;

import java.util.List;

public record TherapyPayload(
        String patientId,
        String mode,
        String title,
        String instruction,
        List<String> prompts,
        String musicHint
) {
}
