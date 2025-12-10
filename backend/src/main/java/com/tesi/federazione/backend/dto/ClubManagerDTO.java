package com.tesi.federazione.backend.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class ClubManagerDTO extends UserDTO {
    private ClubDTO club;
}
