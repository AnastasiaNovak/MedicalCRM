package com.example.medicalcrm.repository;
import com.example.medicalcrm.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByTgId(String tgId);
}
