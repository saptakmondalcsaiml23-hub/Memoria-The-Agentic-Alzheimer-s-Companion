package com.memoria.identity.api;

import com.memoria.identity.entity.CaregiverEntity;
import com.memoria.identity.entity.PatientEntity;
import com.memoria.identity.repository.CaregiverRepository;
import com.memoria.identity.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
class IdentityPersistenceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("pgvector/pgvector:pg16"))
            .withDatabaseName("memoria")
            .withUsername("memoria")
            .withPassword("memoria");

    @DynamicPropertySource
    static void dbProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> true);
    }

    @Autowired
    private CaregiverRepository caregiverRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Test
    void shouldPersistPatientWithCaregiver() {
        CaregiverEntity caregiver = new CaregiverEntity();
        caregiver.setId("c-integ");
        caregiver.setPhoneNumber("+911234567890");
        caregiver.setPreferences("music");
        caregiverRepository.save(caregiver);

        PatientEntity patient = new PatientEntity();
        patient.setId("p-integ");
        patient.setDemographic("female_70_plus");
        patient.setRegion("WB");
        patient.setCaregiver(caregiver);
        patientRepository.save(patient);

        PatientEntity loaded = patientRepository.findById("p-integ").orElseThrow();
        assertEquals("c-integ", loaded.getCaregiver().getId());
    }
}
