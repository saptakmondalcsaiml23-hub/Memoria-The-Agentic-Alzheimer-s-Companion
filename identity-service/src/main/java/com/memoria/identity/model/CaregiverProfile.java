package com.memoria.identity.model;

import jakarta.validation.constraints.NotBlank;

public record CaregiverProfile(
        @NotBlank String id,
        @NotBlank String phoneNumber,
        String preferences
) {
}
