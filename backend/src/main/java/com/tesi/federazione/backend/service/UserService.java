package com.tesi.federazione.backend.service;

import com.tesi.federazione.backend.dto.user.CreateUserDTO;
import com.tesi.federazione.backend.dto.user.UserDTO;
import com.tesi.federazione.backend.model.Athlete;
import com.tesi.federazione.backend.model.User;

import java.util.List;

public interface UserService {
    UserDTO createUser(CreateUserDTO dto);
    UserDTO updateUser(CreateUserDTO dto);
    UserDTO getUserByEmail(String email);
    UserDTO getUserById(String id);

    User createUserEntity(CreateUserDTO dto);
}
