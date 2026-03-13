package com.memoria.identity.repository;

import com.memoria.identity.entity.CaregiverEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaregiverRepository extends JpaRepository<CaregiverEntity, String> {
}
