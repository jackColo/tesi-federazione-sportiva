package com.tesi.federazione.backend.dto.user;

import com.tesi.federazione.backend.dto.club.ClubDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ClubManagerDTO extends UserDTO {
    private ClubDTO club;
}
