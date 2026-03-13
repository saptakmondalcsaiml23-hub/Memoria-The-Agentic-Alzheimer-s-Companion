package com.memoria.identity.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CreatePatientRequest(
        @NotBlank String id,
        @NotBlank String demographic,
        String region,
        @NotBlank String caregiverId
) {
}
