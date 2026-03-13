package com.memoria.identity.api.dto;

public record ViewPatientResponse(
        String id,
        String demographic,
        String region,
        String caregiverId
) {
}
