package com.tesi.federazione.backend.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class UserDTO {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
}
