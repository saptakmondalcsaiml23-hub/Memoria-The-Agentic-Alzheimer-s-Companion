CREATE TABLE caregivers (
    id VARCHAR(64) PRIMARY KEY,
    phone_number VARCHAR(32) NOT NULL,
    preferences TEXT
);

CREATE TABLE patients (
    id VARCHAR(64) PRIMARY KEY,
    demographic VARCHAR(128) NOT NULL,
    region VARCHAR(64),
    caregiver_id VARCHAR(64) NOT NULL REFERENCES caregivers(id)
);
