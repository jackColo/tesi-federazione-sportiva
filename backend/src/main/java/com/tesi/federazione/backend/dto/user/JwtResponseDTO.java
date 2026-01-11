package com.tesi.federazione.backend.dto.user;
import com.tesi.federazione.backend.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponseDTO {
    private String token;
    private String id;
    private String email;
    private Role role;
}