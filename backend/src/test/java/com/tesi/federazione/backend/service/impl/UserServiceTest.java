package com.tesi.federazione.backend.service.impl;

import com.tesi.federazione.backend.dto.user.CreateUserDTO;
import com.tesi.federazione.backend.dto.user.UserDTO;
import com.tesi.federazione.backend.exception.ActionNotAllowedException;
import com.tesi.federazione.backend.exception.ResourceConflictException;
import com.tesi.federazione.backend.exception.ResourceNotFoundException;
import com.tesi.federazione.backend.exception.UnauthorizedException;
import com.tesi.federazione.backend.factory.user.UserCreator;
import com.tesi.federazione.backend.mapper.UserMapper;
import com.tesi.federazione.backend.model.Athlete;
import com.tesi.federazione.backend.model.ClubManager;
import com.tesi.federazione.backend.model.User;
import com.tesi.federazione.backend.model.enums.Role;
import com.tesi.federazione.backend.repository.UserRepository;
import com.tesi.federazione.backend.security.SecurityUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test unitari per lo user service, necessario a testare i metodi:
 * - Creazione di un utente e utilizzo corretto del patter Factory Method per la
 *      creazione dell'entity corretta in base al ruolo
 * - Modifica dell'utente e verifica che non venga creato un utente con email replicata
 * - Verifica dei permessi di accesso ai dati per i metodi get
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private Map<String, UserCreator> creators;

    @InjectMocks
    private UserServiceImpl userService;

    @Nested
    @DisplayName("Tests per: createUser()")
    class CreateUserTests {
        @Test
        @DisplayName("SUCCESSO: uso corretto del factory method")
        public void success() {
            CreateUserDTO createUserDTO = new CreateUserDTO();
            createUserDTO.setFirstName("firstName");
            createUserDTO.setLastName("lastName");
            createUserDTO.setEmail("email");
            createUserDTO.setPassword("password");
            createUserDTO.setRole(Role.ATHLETE.toString());

            // Simuliamo che non siamo un Club Manager
            when(securityUtils.isClubManager()).thenReturn(false);

            // Simuliamo che non esistano utenti con questa email
            when(userRepository.findByEmail(createUserDTO.getEmail())).thenReturn(Optional.empty());

            // Simuliamo pattern Factory per l'atleta
            UserCreator mockCreator = mock(UserCreator.class);
            when(creators.get("ATHLETE")).thenReturn(mockCreator);

            Athlete mockAthlete = new Athlete();
            when(mockCreator.createUser(createUserDTO)).thenReturn(mockAthlete);

            // Simuliamo encoder e salvataggio
            when(passwordEncoder.encode(createUserDTO.getPassword())).thenReturn("encodedPass");
            when(userRepository.save(any(User.class))).thenReturn(mockAthlete);
            when(userMapper.toDTO(any(User.class))).thenReturn(new UserDTO());

            // Invochiamo il metodo da testare
            UserDTO result = userService.createUser(createUserDTO);

            // Verifico che il servizio abbia restituito un risultato
            assertNotNull(result);
            // Verifico che sia stato richiesto il creator corretto
            verify(creators).get("ATHLETE");
            // Verifico che la creazione sia effettivamente stata eseguita dal creator e non dal service
            verify(mockCreator).createUser(createUserDTO);
            // Verifico che la password sia stata cifrata
            verify(passwordEncoder).encode("password");
            // Verifico che sia stata chiamata l'operazione di salvataggio a DB
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("FALLIMENTO: club manager crea utente per altro club")
        public void fail_securityError() {
            CreateUserDTO createUserDTO = new CreateUserDTO();
            createUserDTO.setRole("ATHLETE");
            createUserDTO.setClubId("altroClub");

            // Simuliamo che siamo un Club Manager
            when(securityUtils.isClubManager()).thenReturn(true);

            // Simuliamo l'id del club dell'atleta da creare non sia quello del club gestito dal club manager
            when(securityUtils.isMyClub("altroClub")).thenReturn(false);
            assertThrows(UnauthorizedException.class, () -> {
                userService.createUser(createUserDTO);
            });

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("FALLIMENTO: email indicata già utilizzata")
        public void fail_duplicatedEmail() {
            CreateUserDTO createUserDTO = new CreateUserDTO();
            createUserDTO.setEmail("email@test.it");

            // Simuliamo di non essere un club manager
            when(securityUtils.isClubManager()).thenReturn(false);

            // Simuliamo che esista un utente con la mail indicata per quello nuovo
            when(userRepository.findByEmail(createUserDTO.getEmail())).thenReturn(Optional.of(new User()));

            // Verifichiamo che venga lanciata l'eccezione
            assertThrows(ResourceConflictException.class, () -> {
                userService.createUser(createUserDTO);
            });

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("FALLIMENTO: ruolo inesistente - creator non trovato")
        public void fail_invalidRole() {
            CreateUserDTO createUserDTO = new CreateUserDTO();
            createUserDTO.setRole("ALTRO_RUOLO");
            createUserDTO.setEmail("test@email.ite");

            // Simuliamo di non essere un club manager
            when(securityUtils.isClubManager()).thenReturn(false);

            // Simuliamo che non esista un utente con la mail indicata per quello nuovo
            when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

            // Simuliamo che la mappa non abbia un creator per questo ruolo
            when(creators.get("ALTRO_RUOLO")).thenReturn(null);

            // Verifichiamo che venga lanciata l'eccezione
            assertThrows(ActionNotAllowedException.class, () -> {
                userService.createUser(createUserDTO);
            });

            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Tests per: updateUser()")
    class UpdateUserTests {
        @Test
        @DisplayName("SUCCESSO: modifica salvata correttamente")
        public void success() {
            String userId = "user-123";
            CreateUserDTO updateUserDTO = new CreateUserDTO();
            updateUserDTO.setId(userId);
            updateUserDTO.setEmail("nuovaEmail");
            updateUserDTO.setRole("ATHLETE");
            updateUserDTO.setFirstName("nuovoNome");

            User existingUser = new User();
            existingUser.setId(userId);
            existingUser.setFirstName("Nome");
            existingUser.setEmail("email");
            existingUser.setRole(Role.ATHLETE);
            existingUser.setPassword("passwordCifrataSegreta");

            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
            when(userRepository.findByEmail(updateUserDTO.getEmail())).thenReturn(Optional.empty());

            // Mock factory
            UserCreator mockCreator = mock(UserCreator.class);
            when(creators.get("ATHLETE")).thenReturn(mockCreator);

            Athlete mockAthlete = new Athlete();

            when(userRepository.save(any(User.class))).thenReturn(mockAthlete);
            when(userMapper.toDTO(any(User.class))).thenReturn(new UserDTO());

            // Invochiamo il metodo da testare
            userService.updateUser(updateUserDTO);

            verify(creators).get("ATHLETE");
            verify(userRepository).save(any(User.class));

            // Serve a catturare l'oggetto che verrà salvato tramite userRepository.save()
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

            // Verifichiamo il save e catturiamo l'oggetto passato durante il salvataggio
            verify(userRepository).save(userCaptor.capture());
            User savedUser = userCaptor.getValue();

            // Verifica: La password è rimasta quella vecchia
            assertEquals("passwordCifrataSegreta", savedUser.getPassword());
            // Verifica: L'ID è rimasto lo stesso
            assertEquals("user-123", savedUser.getId());

        }

        @Test
        @DisplayName("FALLIMENTO: email indicata già utilizzata")
        public void fail_duplicatedEmail() {
            String userId = "user-123";
            CreateUserDTO updateUserDTO = new CreateUserDTO();
            updateUserDTO.setId(userId);
            updateUserDTO.setEmail("email@test.it");

            User existingUser = new User();
            existingUser.setId(userId);
            existingUser.setFirstName("Nome");
            existingUser.setEmail("email");
            existingUser.setRole(Role.ATHLETE);
            existingUser.setPassword("passwordCifrataSegreta");

            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

            // Simuliamo che esista un utente con la mail indicata per quello nuovo
            when(userRepository.findByEmail(updateUserDTO.getEmail())).thenReturn(Optional.of(new User()));

            // Verifichiamo che venga lanciata l'eccezione
            assertThrows(ResourceConflictException.class, () -> {
                userService.updateUser(updateUserDTO);
            });

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("FALLIMENTO: utente non trovato")
        public void userNotFound() {
            CreateUserDTO updateUserDTO = new CreateUserDTO();
            updateUserDTO.setId("user-123");

            when(userRepository.findById(updateUserDTO.getId())).thenReturn(Optional.empty());

            // Verifichiamo che venga lanciata l'eccezione
            assertThrows(ResourceNotFoundException.class, () -> {
                userService.updateUser(updateUserDTO);
            });

            verify(userRepository, never()).save(any(User.class));
        }

    }


    @Nested
    @DisplayName("Tests per: changeUserPassword()")
    class ChangeUserPasswordTests {

        @Test
        @DisplayName("FALLIMENTO: utente non trovato")
        public void userNotFound() {
            String userId = "userId";
            String oldPassword = "oldPassword";
            String newPassword = "newPassword";

            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            // Verifichiamo che venga lanciata l'eccezione
            assertThrows(ResourceNotFoundException.class, () -> {
                userService.changeUserPassword(userId, oldPassword, newPassword);
            });

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("FALLIMENTO: modifica password di altro utente")
        public void fail_unauthorizedRequest() {
            String currentUserId = "userId";
            String userId = "otherUserId";
            String oldPassword = "oldPassword";
            String newPassword = "newPassword";

            User user = new User();
            user.setId(userId);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(securityUtils.getCurrentUserId()).thenReturn(currentUserId);

            // Verifichiamo che venga lanciata l'eccezione
            assertThrows(UnauthorizedException.class, () -> {
                userService.changeUserPassword(userId, oldPassword, newPassword);
            });

            verify(userRepository, never()).save(any(User.class));
        }


        @Test
        @DisplayName("FALLIMENTO: vecchia password errata")
        public void fail_passwordMismatch() {
            String userId = "userId";
            String wrongOldPassword = "wrongOldPassword";
            String newPassword = "newPassword";
            String dbEncodedPassword = "encodedRealPassword";

            User user = new User();
            user.setId(userId);
            user.setPassword(dbEncodedPassword);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(securityUtils.getCurrentUserId()).thenReturn(userId);
            when(passwordEncoder.matches(wrongOldPassword, user.getPassword())).thenReturn(false);

            // Verifichiamo che venga lanciata l'eccezione
            assertThrows(ActionNotAllowedException.class, () -> {
                userService.changeUserPassword(userId, wrongOldPassword, newPassword);
            });

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("SUCCESSO: password modificata correttamente")
        public void success() {
            String userId = "userId";
            String oldPassword = "oldPassword";
            String newPassword = "newPassword";
            String dbEncodedPassword = "encodedPassword";
            String dbEncodedNewPassword = "encodedNewPassword";

            User user = new User();
            user.setId(userId);
            user.setPassword(dbEncodedPassword);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(securityUtils.getCurrentUserId()).thenReturn(userId);
            when(passwordEncoder.matches(oldPassword, user.getPassword())).thenReturn(true);
            when(passwordEncoder.encode(newPassword)).thenReturn(dbEncodedNewPassword);

            userService.changeUserPassword(userId, oldPassword, newPassword);

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            User savedUser = userCaptor.getValue();
            assertEquals(dbEncodedNewPassword, savedUser.getPassword());
            verify(passwordEncoder).encode(newPassword);
        }



    }

    @Nested
    @DisplayName("Tests per: getUserByEmail()")
    class getUserByEmailTests {
        @Test
        @DisplayName("SUCCESSO: club manager richiede dati di suo atleta")
        public void success() {
            String managerClubId = "CLUB_A";
            String targetEmail = "atleta@test.com";

            // 1. Sicurezza: Sono un Manager del CLUB_A
            when(securityUtils.isAthlete()).thenReturn(false);
            when(securityUtils.isClubManager()).thenReturn(true);
            when(securityUtils.getUserClub()).thenReturn(managerClubId);

            // 2. DB: Restituisce un atleta che appartiene al CLUB_A
            Athlete targetAthlete = new Athlete();
            targetAthlete.setEmail(targetEmail);
            targetAthlete.setClubId(managerClubId);

            when(userRepository.findByEmail(targetEmail)).thenReturn(Optional.of(targetAthlete));
            when(userMapper.toDTO(any(User.class))).thenReturn(new UserDTO());

            UserDTO result = userService.getUserByEmail(targetEmail);

            assertNotNull(result);
        }

        @Test
        @DisplayName("FALLIMENTO: atleta tenta di accedere a dati di altro atleta")
        public void fail_athleteUnauthorized() {
            String myEmail = "me@test.com";
            String targetEmail = "other@test.com";

            when(securityUtils.isAthlete()).thenReturn(true);
            when(securityUtils.getCurrentUserEmail()).thenReturn(myEmail);

            assertThrows(UnauthorizedException.class, () -> {
                userService.getUserByEmail(targetEmail);
            });

            verify(userRepository, never()).findByEmail(anyString());
        }

        @Test
        @DisplayName("FALLIMENTO: club manager tenta di accedere a dati di atleti di altri club")
        public void fail_clubManagerUnauthorized_otherAthlete() {
            String managerClubId = "CLUB_A";
            String targetEmail = "rivale@test.com";

            // 1. Sicurezza: Sono Manager CLUB_A
            when(securityUtils.isAthlete()).thenReturn(false);
            when(securityUtils.isClubManager()).thenReturn(true);
            when(securityUtils.getUserClub()).thenReturn(managerClubId);

            // 2. DB: Restituisce atleta del CLUB_B
            Athlete targetAthlete = new Athlete();
            targetAthlete.setEmail(targetEmail);
            targetAthlete.setClubId("CLUB_B");

            when(userRepository.findByEmail(targetEmail)).thenReturn(Optional.of(targetAthlete));

            assertThrows(UnauthorizedException.class, () -> {
                userService.getUserByEmail(targetEmail);
            });
        }

        @Test
        @DisplayName("FALLIMENTO: club manager tenta di accedere a dati di altro club manager")
        public void fail_clubManagerUnauthorized_otherClubManager() {
            String managerEmail = "mia_email";
            String targetEmail = "manager_club_b@test.com";

            // 1. Sicurezza: Sono un club manager
            when(securityUtils.isAthlete()).thenReturn(false);
            when(securityUtils.isClubManager()).thenReturn(true);
            when(securityUtils.getCurrentUserEmail()).thenReturn(managerEmail);

            // 2. DB: Restituisce altro club manager
            ClubManager target = new ClubManager();
            target.setEmail(targetEmail);
            target.setManagedClub("CLUB_B");

            when(userRepository.findByEmail(targetEmail)).thenReturn(Optional.of(target));

            assertThrows(UnauthorizedException.class, () -> {
                userService.getUserByEmail(targetEmail);
            });
        }

        @Test
        @DisplayName("FALLIMENTO: utente non trovato")
        public void fail_userNotFound() {
            String targetEmail = "email@test.com";

            when(securityUtils.isAthlete()).thenReturn(false);

            when(userRepository.findByEmail(targetEmail)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> {
                userService.getUserByEmail(targetEmail);
            });
        }
    }

    @Nested
    @DisplayName("Tests per: getUserById()")
    class getUserByIdTests {
        @Test
        @DisplayName("SUCCESSO: club manager richiede dati di suo atleta")
        public void success() {
            String managerClubId = "CLUB_A";
            String targetId = "1234";

            // 1. Sicurezza: Sono un Manager del CLUB_A
            when(securityUtils.isAthlete()).thenReturn(false);
            when(securityUtils.isClubManager()).thenReturn(true);
            when(securityUtils.getUserClub()).thenReturn(managerClubId);

            // 2. DB: Restituisce un atleta che appartiene al CLUB_A
            Athlete targetAthlete = new Athlete();
            targetAthlete.setId(targetId);
            targetAthlete.setClubId(managerClubId);

            when(userRepository.findById(targetId)).thenReturn(Optional.of(targetAthlete));
            when(userMapper.toDTO(any(User.class))).thenReturn(new UserDTO());

            UserDTO result = userService.getUserById(targetId);

            assertNotNull(result);
        }

        @Test
        @DisplayName("FALLIMENTO: atleta tenta di accedere a dati di altro atleta")
        public void fail_athleteUnauthorized() {
            String myId = "1234";
            String targetId = "4321";

            when(securityUtils.isAthlete()).thenReturn(true);
            when(securityUtils.getCurrentUserId()).thenReturn(myId);

            assertThrows(UnauthorizedException.class, () -> {
                userService.getUserById(targetId);
            });

            verify(userRepository, never()).findById(anyString());
        }

        @Test
        @DisplayName("FALLIMENTO: club manager tenta di accedere a dati di atleti di altri club")
        public void fail_clubManagerUnauthorized_otherAthlete() {
            String managerClubId = "CLUB_A";
            String targetId = "1234";

            // 1. Sicurezza: Sono Manager CLUB_A
            when(securityUtils.isAthlete()).thenReturn(false);
            when(securityUtils.isClubManager()).thenReturn(true);
            when(securityUtils.getUserClub()).thenReturn(managerClubId);

            // 2. DB: Restituisce atleta del CLUB_B
            Athlete targetAthlete = new Athlete();
            targetAthlete.setId(targetId);
            targetAthlete.setClubId("CLUB_B");

            when(userRepository.findById(targetId)).thenReturn(Optional.of(targetAthlete));

            assertThrows(UnauthorizedException.class, () -> {
                userService.getUserById(targetId);
            });
        }

        @Test
        @DisplayName("FALLIMENTO: club manager tenta di accedere a dati di altro club manager")
        public void fail_clubManagerUnauthorized_otherClubManager() {
            String managerId = "4321";
            String targetId = "1234";

            // 1. Sicurezza: Sono un club manager
            when(securityUtils.isAthlete()).thenReturn(false);
            when(securityUtils.isClubManager()).thenReturn(true);
            when(securityUtils.getCurrentUserId()).thenReturn(managerId);

            // 2. DB: Restituisce altro club manager
            ClubManager target = new ClubManager();
            target.setId(targetId);
            target.setManagedClub("CLUB_B");

            when(userRepository.findById(targetId)).thenReturn(Optional.of(target));

            assertThrows(UnauthorizedException.class, () -> {
                userService.getUserById(targetId);
            });
        }

        @Test
        @DisplayName("FALLIMENTO: utente non trovato")
        public void fail_userNotFound() {
            String targetId = "1234";

            when(securityUtils.isAthlete()).thenReturn(false);

            when(userRepository.findById(targetId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> {
                userService.getUserById(targetId);
            });
        }
    }

}
