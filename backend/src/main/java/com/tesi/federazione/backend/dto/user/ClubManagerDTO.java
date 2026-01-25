package com.tesi.federazione.backend.dto.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * DTO specifico per la visualizzazione dei dettagli di un Club Manager.
 * Estende UserDTO aggiungendo il riferimento al club gestito.
 * L'annotazione @Data di Lombok genera automaticamente i metodi getter e setter.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ClubManagerDTO extends UserDTO {
    private String clubId;
}
