package com.memoria.orchestrator.integration.dto;

public record ProviderOutcome(
        boolean success,
        String provider,
        String message
) {
}
