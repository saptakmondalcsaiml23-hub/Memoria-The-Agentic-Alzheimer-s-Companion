package com.memoria.identity.api;

import com.memoria.identity.api.dto.CreateCaregiverRequest;
import com.memoria.identity.api.dto.CreatePatientRequest;
import com.memoria.identity.api.dto.ViewCaregiverResponse;
import com.memoria.identity.api.dto.ViewPatientResponse;
import com.memoria.identity.model.CaregiverProfile;
import com.memoria.identity.model.PatientProfile;
import com.memoria.identity.service.IdentityService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class IdentityController {

    private final IdentityService identityService;

    public IdentityController(IdentityService identityService) {
        this.identityService = identityService;
    }

    @PostMapping("/caregivers")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public ViewCaregiverResponse createCaregiver(@Valid @RequestBody CreateCaregiverRequest request) {
        CaregiverProfile profile = identityService.createCaregiver(request);
        return new ViewCaregiverResponse(profile.id(), profile.phoneNumber(), profile.preferences());
    }

    @GetMapping("/caregivers/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CAREGIVER')")
    public ViewCaregiverResponse getCaregiver(@PathVariable String id) {
        CaregiverProfile profile = identityService.getCaregiver(id);
        return new ViewCaregiverResponse(profile.id(), profile.phoneNumber(), profile.preferences());
    }

    @PostMapping("/patients")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public ViewPatientResponse createPatient(@Valid @RequestBody CreatePatientRequest request) {
        PatientProfile profile = identityService.createPatient(request);
        return new ViewPatientResponse(profile.id(), profile.demographic(), profile.region(), profile.caregiverId());
    }

    @GetMapping("/patients/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CAREGIVER')")
    public ViewPatientResponse getPatient(@PathVariable String id) {
        PatientProfile profile = identityService.getPatient(id);
        return new ViewPatientResponse(profile.id(), profile.demographic(), profile.region(), profile.caregiverId());
    }

    @GetMapping("/status")
    public Map<String, String> status() {
        return Map.of("service", "identity-service", "status", "ok");
    }
}
