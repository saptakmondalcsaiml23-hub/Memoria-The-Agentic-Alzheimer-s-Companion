package com.memoria.identity.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCaregiverRequest(
        @NotBlank String id,
        @NotBlank String phoneNumber,
        String preferences
) {
}
