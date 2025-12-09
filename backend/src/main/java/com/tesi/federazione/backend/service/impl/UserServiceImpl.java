package com.tesi.federazione.backend.service.impl;

import com.tesi.federazione.backend.dto.CreateUserDTO;
import com.tesi.federazione.backend.enums.Role;
import com.tesi.federazione.backend.model.Athlete;
import com.tesi.federazione.backend.model.ClubManager;
import com.tesi.federazione.backend.model.FederationManager;
import com.tesi.federazione.backend.model.User;
import com.tesi.federazione.backend.repository.UserRepository;
import com.tesi.federazione.backend.security.JwtUtils;
import com.tesi.federazione.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,  PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(CreateUserDTO dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use.");
        }
        User user = createUserEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        return userRepository.save(user);
    }

    private User createUserEntity(CreateUserDTO dto) {
        User user;
        String role = dto.getRole();

        if (role.equals(Role.ATHLETE.name())) {
            user = new Athlete();
        } else if (role.equals(Role.CLUB_MANAGER.name())) {
            user = new ClubManager();
        } else if (role.equals(Role.FEDERATION_MANAGER.name())) {
            user = new FederationManager();
        } else {
            throw new IllegalArgumentException("Role not specified or invalid.");
        }

        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());

        return user;
    }
}
