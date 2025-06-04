package com.example.medicalcrm.entity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data

public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String city;
    private String country;
    private String phone;
    private String email;
    private String vkId;
    private String tgId;
    private String username;
    private String instaId;
    private String typeOfPayment;

    private LocalDate  operationDate;
}
