package com.example.medicalcrm.repository;
import com.example.medicalcrm.entity.Application;
import com.example.medicalcrm.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

}
