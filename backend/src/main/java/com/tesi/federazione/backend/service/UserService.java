package com.tesi.federazione.backend.service;

import com.tesi.federazione.backend.dto.CreateUserDTO;
import com.tesi.federazione.backend.model.User;

public interface UserService {
    User createUser(CreateUserDTO dto);
}
