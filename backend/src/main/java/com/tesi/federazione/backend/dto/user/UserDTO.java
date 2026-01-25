package com.tesi.federazione.backend.dto.user;

import lombok.Data;
import org.springframework.data.annotation.Id;
/**
 * DTO base per il trasferimento dei dati utente.
 * Contiene le informazioni comuni visibili a tutti i livelli.
 * L'annotazione @Data di Lombok genera automaticamente i metodi getter e setter.
 */
@Data
public class UserDTO {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
}
