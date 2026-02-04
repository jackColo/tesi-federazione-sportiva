package com.tesi.federazione.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tesi.federazione.backend.dto.user.ChangePasswordRequestDTO;
import com.tesi.federazione.backend.dto.user.CreateUserDTO;
import com.tesi.federazione.backend.dto.user.UserDTO;
import com.tesi.federazione.backend.model.enums.Role;
import com.tesi.federazione.backend.security.JwtUtils;
import com.tesi.federazione.backend.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Test per GET email/{email}")
    void getUserByEmailTest() throws Exception {
        String email = "test@email.com";
        UserDTO userDTO = new UserDTO();
        userDTO.setId("userId");
        userDTO.setEmail(email);
        userDTO.setRole(Role.ATHLETE.toString());

        when(userService.getUserByEmail(email)).thenReturn(userDTO);

        mockMvc.perform(get("/api/user/email/{email}", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.id").value("userId"));
    }

    @Test
    @DisplayName("Test per GET /{id}")
    void getUserByIdTest() throws Exception {
        String id = "userId";
        UserDTO userDTO = new UserDTO();
        userDTO.setId(id);
        userDTO.setEmail("mario.rossi@email.it");

        when(userService.getUserById(id)).thenReturn(userDTO);

        mockMvc.perform(get("/api/user/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.email").value("mario.rossi@email.it"));
    }

    @Test
    @DisplayName("Test per POST /create")
    void createUserTest() throws Exception {
        CreateUserDTO createUserDto = new CreateUserDTO();
        createUserDto.setEmail("new@user.com");
        createUserDto.setPassword("password");

        UserDTO responseDto = new UserDTO();
        responseDto.setId("userId");
        responseDto.setEmail("new@user.com");

        when(userService.createUser(createUserDto)).thenReturn(responseDto);

        mockMvc.perform(post("/api/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("userId"))
                .andExpect(jsonPath("$.email").value("new@user.com"));
    }

    @Test
    @DisplayName("Test per PATCH /update/{id}")
    void updateUserTest() throws Exception {
        String id = "userId";
        CreateUserDTO updateDto = new CreateUserDTO();
        updateDto.setEmail("updated@email.com");

        UserDTO responseDto = new UserDTO();
        responseDto.setId(id);
        responseDto.setEmail("updated@email.com");

        when(userService.updateUser(any(CreateUserDTO.class))).thenReturn(responseDto);

        mockMvc.perform(patch("/api/user/update/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.email").value("updated@email.com"));
    }

    @Test
    @DisplayName("Test per POST /change-password/{id}")
    void changeUserPasswordTest() throws Exception {
        String userId = "userId";
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";
        ChangePasswordRequestDTO request = new ChangePasswordRequestDTO();
        request.setOldPassword(oldPassword);
        request.setNewPassword(newPassword);

        doNothing().when(userService).changeUserPassword(userId, oldPassword, newPassword);

        mockMvc.perform(post("/api/user/change-password/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // 5. Verifichiamo che il service sia stato chiamato con i parametri giusti
        verify(userService).changeUserPassword(userId, oldPassword, newPassword);
    }

    @Test
    @DisplayName("Test per GET /find-by-role/{role}")
    void findByRoleTest() throws Exception {
        Role role = Role.FEDERATION_MANAGER;
        UserDTO userDTO = new UserDTO();
        userDTO.setId("userId");
        userDTO.setRole(Role.FEDERATION_MANAGER.toString());

        when(userService.getAllByRole(role)).thenReturn(List.of(userDTO));

        mockMvc.perform(get("/api/user/find-by-role/{role}", role)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].role").value(role.toString()))
                .andExpect(jsonPath("$[0].id").value("userId"));
    }
}