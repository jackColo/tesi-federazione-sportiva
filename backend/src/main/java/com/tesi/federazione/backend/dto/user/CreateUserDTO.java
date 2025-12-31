package com.tesi.federazione.backend.dto.user;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateUserDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String role;

    // campi specifici per gli atleti (opzionali per gli altri ruoli)
    private LocalDate birthDate;
    private Float weight;
    private Float height;
    private String clubId;
    private String medicalCertificateNumber;
    private LocalDate medicalCertificateExpireDate;
}