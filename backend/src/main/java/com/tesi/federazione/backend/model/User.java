package com.tesi.federazione.backend.model;

import com.tesi.federazione.backend.model.enums.Role;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
/**
 * Entità base che rappresenta un utente generico, mappata sulla collection MongoDB "users".
 * Implementa l'interfaccia UserDetails di Spring Security, permettendo l'integrazione nativa con il meccanismo di autenticazione.
 * Ha le specializzazioni Athlete, ClubManager e FederationManager.
 * Sfrutta il polimorfismo di MongoDB (tutti i tipi sono salvati nella stessa collection).
 * L'annotazione @Data di Lombok genera automaticamente i metodi getter e setter.
 */
@Data
@Document(collection = "users")
public class User implements UserDetails {
    @Id
    private String id;

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Role role;

    /**
     * Restituisce le autorità concesse all'utente, che in questo progetto corrisponde al ruolo dell'utente.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(String.valueOf(this.role)));
    }

    /**
     * Sovrascrivo getUsername di UserDetails per indicare al sistema di utilizzare l'email come username
     */
    @Override
    public String getUsername() {
        return this.getEmail();
    }

}
