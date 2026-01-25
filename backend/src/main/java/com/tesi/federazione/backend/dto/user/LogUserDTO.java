package com.tesi.federazione.backend.dto.user;
import lombok.Data;

/**
 * DTO utilizzato per la login con le credenziali dell'utente.
 * L'annotazione @Data di Lombok genera automaticamente i metodi getter e setter.
 */
@Data
public class LogUserDTO {
    private String email;
    private String password;
}