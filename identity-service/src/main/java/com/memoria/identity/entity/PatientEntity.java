package com.memoria.identity.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "patients")
public class PatientEntity {

    @Id
    @Column(nullable = false, length = 64)
    private String id;

    @Column(nullable = false, length = 128)
    private String demographic;

    @Column(length = 64)
    private String region;

    @ManyToOne(optional = false)
    @JoinColumn(name = "caregiver_id", nullable = false)
    private CaregiverEntity caregiver;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDemographic() {
        return demographic;
    }

    public void setDemographic(String demographic) {
        this.demographic = demographic;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public CaregiverEntity getCaregiver() {
        return caregiver;
    }

    public void setCaregiver(CaregiverEntity caregiver) {
        this.caregiver = caregiver;
    }
}
