package com.tesi.federazione.backend.service.impl;

import com.tesi.federazione.backend.dto.CreateUserDTO;
import com.tesi.federazione.backend.factory.user.UserCreator;
import com.tesi.federazione.backend.model.User;
import com.tesi.federazione.backend.repository.UserRepository;
import com.tesi.federazione.backend.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final Map<String, UserCreator> creators;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(Map<String, UserCreator> creators,UserRepository userRepository,  PasswordEncoder passwordEncoder) {
        this.creators = creators;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(CreateUserDTO dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use.");
        }

        String roleKey = dto.getRole().toLowerCase() + "Creator";

        UserCreator creator = creators.get(roleKey);

        User user = creator.createUser();

        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        return userRepository.save(user);
    }
}
