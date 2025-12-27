package com.tesi.federazione.backend.factory.user;

import com.tesi.federazione.backend.dto.user.CreateUserDTO;
import com.tesi.federazione.backend.model.User;

public interface UserCreator {
    User createUser(CreateUserDTO dto);
}
