package com.tesi.federazione.backend.mapper;

import com.tesi.federazione.backend.dto.user.UserDTO;
import com.tesi.federazione.backend.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setRole(String.valueOf(user.getRole()));
        return dto;
    }
}