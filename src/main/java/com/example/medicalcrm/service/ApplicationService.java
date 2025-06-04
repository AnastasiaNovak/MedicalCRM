package com.example.medicalcrm.service;
import com.example.medicalcrm.dto.ApplicationRequestDto;
import com.example.medicalcrm.entity.Application;
import com.example.medicalcrm.repository.ApplicationRepository;
import com.example.medicalcrm.repository.CampaignRepository;
import com.example.medicalcrm.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private CampaignRepository campaignRepository;

    public List <Application> getAllApplications(){
        return applicationRepository.findAll();
    }

    public Optional <Application> getApplicationById(Long id){
        return applicationRepository.findById(id);
    }

    public Application saveApplication(Application application){
        return applicationRepository.save(application);
    }

    public void deleteApplication(Long id){
        applicationRepository.deleteById(id);
    }

    public Application create(ApplicationRequestDto dto) {
        Application application = dto.toEntity();
        if (dto.getPatientId() != null) {
            patientRepository.findById(dto.getPatientId()).ifPresent(application::setPatient);
        }

        if (dto.getCampaignId() != null) {
            campaignRepository.findById(dto.getCampaignId()).ifPresent(application::setCampaign);
        }
        return applicationRepository.save(application);
    }

    public void deleteApplicationById(Long id) {
        applicationRepository.deleteById(id);
    }
}
