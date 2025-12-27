package com.tesi.federazione.backend.mapper;

import com.tesi.federazione.backend.dto.user.CreateUserDTO;
import com.tesi.federazione.backend.dto.user.UserDTO;
import com.tesi.federazione.backend.model.enums.Role;
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

    public User toEntity(CreateUserDTO dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        if (dto.getRole().equals(String.valueOf(Role.ATHLETE))) {
            user.setRole(Role.ATHLETE);
        } else if (dto.getRole().equals(String.valueOf(Role.FEDERATION_MANAGER))) {
            user.setRole(Role.FEDERATION_MANAGER);
        } else if (dto.getRole().equals(String.valueOf(Role.CLUB_MANAGER))) {
            user.setRole(Role.CLUB_MANAGER);
        }
        return user;
    }
}