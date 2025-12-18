package com.tesi.federazione.backend.dto.user;

import lombok.Data;

@Data
public class CreateUserDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String role;
}