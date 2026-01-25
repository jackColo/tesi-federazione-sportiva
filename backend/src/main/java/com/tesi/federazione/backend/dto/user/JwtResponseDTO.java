package com.tesi.federazione.backend.dto.user;
import com.tesi.federazione.backend.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO di risposta inviato al client dopo un login avvenuto con successo.
 * Contiene il token JWT necessario per le chiamate successive e i dettagli essenziali dell'utente loggato.
 * L'annotazione @Data di Lombok genera automaticamente i metodi getter e setter.
 * L'annotazione @AllArgsConstructor di Lombok genere automaticamente il costruttore con tutti i parametri
 */
@Data
@AllArgsConstructor
public class JwtResponseDTO {
    private String token;
    private String id;
    private String email;
    private Role role;
}