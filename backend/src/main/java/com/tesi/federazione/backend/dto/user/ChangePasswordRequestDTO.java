package com.tesi.federazione.backend.dto.user;
import lombok.Data;

/**
 * DTO utilizzato per richiedere la modifica della password dell'utente.
 * L'annotazione @Data di Lombok genera automaticamente i metodi getter e setter.
 */
@Data
public class ChangePasswordRequestDTO {
    private String oldPassword;
    private String newPassword;
}