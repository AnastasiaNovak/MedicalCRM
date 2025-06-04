package com.example.medicalcrm.service;
import com.example.medicalcrm.dto.PatientRequestDto;
import com.example.medicalcrm.entity.Patient;
import com.example.medicalcrm.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }

    public Patient savePatient(Patient patient) {
        return patientRepository.save(patient);
    }

    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }

    public Patient create(PatientRequestDto dto) {
        Patient patient = dto.toEntity();
        return patientRepository.save(patient);
    }

    public Patient getByTelegramId(String tgId) {
        return patientRepository.findByTgId(tgId).orElse(null);
    }

}
