package com.tesi.federazione.backend.dto.user;

import com.tesi.federazione.backend.dto.club.ClubDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class AthleteDTO extends UserDTO {
    private LocalDate birthDate;
    private ClubDTO club;
}
