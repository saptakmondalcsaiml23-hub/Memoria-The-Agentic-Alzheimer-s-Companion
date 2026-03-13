package com.memoria.identity.service;

import com.memoria.identity.api.dto.CreateCaregiverRequest;
import com.memoria.identity.api.dto.CreatePatientRequest;
import com.memoria.identity.entity.CaregiverEntity;
import com.memoria.identity.entity.PatientEntity;
import com.memoria.identity.model.CaregiverProfile;
import com.memoria.identity.model.PatientProfile;
import com.memoria.identity.repository.CaregiverRepository;
import com.memoria.identity.repository.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class IdentityService {

    private final CaregiverRepository caregiverRepository;
    private final PatientRepository patientRepository;

    public IdentityService(CaregiverRepository caregiverRepository, PatientRepository patientRepository) {
        this.caregiverRepository = caregiverRepository;
        this.patientRepository = patientRepository;
    }

    @Transactional
    public CaregiverProfile createCaregiver(CreateCaregiverRequest request) {
        CaregiverEntity entity = new CaregiverEntity();
        entity.setId(request.id());
        entity.setPhoneNumber(request.phoneNumber());
        entity.setPreferences(request.preferences());
        CaregiverEntity saved = caregiverRepository.save(entity);
        return new CaregiverProfile(saved.getId(), saved.getPhoneNumber(), saved.getPreferences());
    }

    @Transactional
    public PatientProfile createPatient(CreatePatientRequest request) {
        CaregiverEntity caregiver = caregiverRepository.findById(request.caregiverId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown caregiverId"));

        PatientEntity entity = new PatientEntity();
        entity.setId(request.id());
        entity.setDemographic(request.demographic());
        entity.setRegion(request.region());
        entity.setCaregiver(caregiver);

        PatientEntity saved = patientRepository.save(entity);
        return new PatientProfile(saved.getId(), saved.getDemographic(), saved.getRegion(), saved.getCaregiver().getId());
    }

    @Transactional(readOnly = true)
    public CaregiverProfile getCaregiver(String id) {
        CaregiverEntity entity = caregiverRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Caregiver not found"));
        return new CaregiverProfile(entity.getId(), entity.getPhoneNumber(), entity.getPreferences());
    }

    @Transactional(readOnly = true)
    public PatientProfile getPatient(String id) {
        PatientEntity entity = patientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));
        return new PatientProfile(entity.getId(), entity.getDemographic(), entity.getRegion(), entity.getCaregiver().getId());
    }
}
