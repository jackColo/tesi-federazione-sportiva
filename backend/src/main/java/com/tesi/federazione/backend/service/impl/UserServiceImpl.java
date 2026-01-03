package com.tesi.federazione.backend.service.impl;

import com.tesi.federazione.backend.dto.user.CreateUserDTO;
import com.tesi.federazione.backend.dto.user.UserDTO;
import com.tesi.federazione.backend.exception.ResourceNotFoundException;
import com.tesi.federazione.backend.factory.user.UserCreator;
import com.tesi.federazione.backend.mapper.UserMapper;
import com.tesi.federazione.backend.model.User;
import com.tesi.federazione.backend.repository.UserRepository;
import com.tesi.federazione.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final Map<String, UserCreator> creators;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserDTO createUser(CreateUserDTO dto) {
        User savedUser = createUserEntity(dto);
        return userMapper.toDTO(savedUser);
    }


    @Override
    public UserDTO updateUser(CreateUserDTO dto) {
        User existingUser = userRepository.findById(dto.getId()).orElseThrow(() -> new ResourceNotFoundException("Utente con id " + dto.getId() + " non trovato"));
        String existingEmail = existingUser.getEmail();
        if (!existingEmail.equals(dto.getEmail())) {
            if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email already in use.");
            }
        }

        User user = createEntity(dto);
        user.setId(dto.getId());
        user.setPassword(existingUser.getPassword());

        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);

    }

    @Override
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Utente " + email + " non trovato"));
        return userMapper.toDTO(user);
    }

    @Override
    public UserDTO getUserById(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Utente con id " + id + " non trovato"));
        return userMapper.toDTO(user);
    }

    @Override
    public User createUserEntity(CreateUserDTO dto) {

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use.");
        }

        User user = createEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        return userRepository.save(user);
    }

    private User createEntity(CreateUserDTO dto) {
        String roleKey = dto.getRole();

        UserCreator creator = creators.get(roleKey);

        User user = creator.createUser(dto);

        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());

        return user;
    }
}
