package com.tesi.federazione.backend.service;

import com.tesi.federazione.backend.dto.user.CreateUserDTO;
import com.tesi.federazione.backend.dto.user.UserDTO;
import com.tesi.federazione.backend.model.Athlete;
import com.tesi.federazione.backend.model.User;

import java.util.List;

public interface UserService {
    User createUser(CreateUserDTO dto);
    User updateUser(CreateUserDTO dto);
    User getUserByEmail(String email);
    User getUserById(String id);
}
