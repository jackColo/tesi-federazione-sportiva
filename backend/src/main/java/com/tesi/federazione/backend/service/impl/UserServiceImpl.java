package com.tesi.federazione.backend.service.impl;

import com.tesi.federazione.backend.dto.user.CreateUserDTO;
import com.tesi.federazione.backend.dto.user.UserDTO;
import com.tesi.federazione.backend.factory.user.UserCreator;
import com.tesi.federazione.backend.model.Athlete;
import com.tesi.federazione.backend.model.User;
import com.tesi.federazione.backend.repository.UserRepository;
import com.tesi.federazione.backend.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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

        User user = createEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        return userRepository.save(user);
    }

    @Override
    public User updateUser(CreateUserDTO dto) {
        User existingUser = getUserById(dto.getId());
        String existingEmail = existingUser.getEmail();
        if (!existingEmail.equals(dto.getEmail())) {
            if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email already in use.");
            }
        }

        User user = createEntity(dto);
        user.setId(dto.getId());
        user.setPassword(existingUser.getPassword());

        return userRepository.save(user);

    }

    @Override
    public User getUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found.");
        }
        return user.get();
    }

    @Override
    public User getUserById(String id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found.");
        }
        return user.get();
    }

    @Override
    public List<Athlete> getUsersByClubId(String clubId) {
        return userRepository.findAllByClubId(clubId);
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
