package com.tesi.federazione.backend.controller;

import com.tesi.federazione.backend.dto.CreateUserDTO;
import com.tesi.federazione.backend.dto.JwtResponseDTO;
import com.tesi.federazione.backend.dto.LogUserDTO;
import com.tesi.federazione.backend.enums.Role;
import com.tesi.federazione.backend.model.Athlete;
import com.tesi.federazione.backend.model.ClubManager;
import com.tesi.federazione.backend.model.FederationManager;
import com.tesi.federazione.backend.model.User;
import com.tesi.federazione.backend.repository.UserRepository;
import com.tesi.federazione.backend.security.JwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> authenticateUser(@RequestBody LogUserDTO loginDTO) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String jwt = jwtUtils.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponseDTO(jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody CreateUserDTO createUserDTO) {

        if (userRepository.findByEmail(createUserDTO.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        User user;
        String role = createUserDTO.getRole();

        if (role.equals(Role.ATHLETE.name())) {
            user = new Athlete();
        } else if (role.equals(Role.CLUB_MANAGER.name())) {
            user = new ClubManager();
        } else if (role.equals(Role.FEDERATION_MANAGER.name())) {
            user = new FederationManager();
        } else {
            return ResponseEntity.badRequest().body("Error: Role not specified!");
        }

        user.setEmail(createUserDTO.getEmail());
        user.setFirstName(createUserDTO.getFirstName());
        user.setLastName(createUserDTO.getLastName());
        user.setPassword(passwordEncoder.encode(createUserDTO.getPassword()));

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }
}