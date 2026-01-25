package com.tesi.federazione.backend.dto.user;

import com.tesi.federazione.backend.model.enums.GenderEnum;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO di input unificato per la creazione di nuovi utenti.
 * È progettato per coprire i campi di tutte le tipologie di utente.
 * Viene delegata ai servizi l'estrazione dei campi pertinenti in base al ruolo.
 * L'annotazione @Data di Lombok genera automaticamente i metodi getter e setter.
 */
@Data
public class CreateUserDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String role;

    // campi specifici per gli atleti (ignorati per gli altri ruoli)
    private LocalDate birthDate;
    private Float weight;
    private Float height;
    private GenderEnum gender;
    private String medicalCertificateNumber;
    private LocalDate medicalCertificateExpireDate;

    // campo comune a club manager e atleti (ignorato per gli altri ruoli)
    private String clubId;
}