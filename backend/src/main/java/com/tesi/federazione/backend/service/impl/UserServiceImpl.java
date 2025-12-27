package com.tesi.federazione.backend.service.impl;

import com.tesi.federazione.backend.dto.user.CreateUserDTO;
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

    public UserServiceImpl(UserRepository userRepository, Map<String, UserCreator> creators, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.creators = creators;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(CreateUserDTO dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use.");
        }

        String roleKey = dto.getRole();

        UserCreator creator = creators.get(roleKey);

        User user = creator.createUser(dto);

        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        return userRepository.save(user);
    }
}
