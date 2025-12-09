package com.tesi.federazione.backend.service;

import com.tesi.federazione.backend.dto.ClubManagerDTO;
import com.tesi.federazione.backend.dto.CreateClubDTO;
import com.tesi.federazione.backend.dto.CreateUserDTO;
import com.tesi.federazione.backend.model.User;

public interface UserService {
    public User createUser(CreateUserDTO dto);
}
