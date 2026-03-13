package com.memoria.identity.model;

import jakarta.validation.constraints.NotBlank;

public record PatientProfile(
        @NotBlank String id,
        @NotBlank String demographic,
        String region,
        @NotBlank String caregiverId
) {
}
