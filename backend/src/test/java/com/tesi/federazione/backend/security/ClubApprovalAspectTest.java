package com.tesi.federazione.backend.security;

import com.tesi.federazione.backend.dto.club.ClubDTO;
import com.tesi.federazione.backend.exception.UnauthorizedException;
import com.tesi.federazione.backend.model.ClubManager;
import com.tesi.federazione.backend.model.enums.AffiliationStatus;
import com.tesi.federazione.backend.model.enums.Role;
import com.tesi.federazione.backend.repository.UserRepository;
import com.tesi.federazione.backend.service.ClubService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClubApprovalAspectTest {

    private SecurityContext securityContext;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        securityContext = mock(SecurityContext.class);
        authentication = mock(Authentication.class);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Mock
    private UserRepository userRepository;
    @Mock
    private ClubService clubService;

    @InjectMocks
    private ClubApprovalAspect clubApprovalAspect;


    @Nested
    @DisplayName("Test per: checkClubApproval")
    class CheckClubApprovalTests {

        @Test
        @DisplayName("FALLIMENTO: blocco per utente non trovato")
        public void fail_userNotFound() {
            String email = "test@gmail.com";
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(email);
            when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

            assertThrows(BadCredentialsException.class, () -> clubApprovalAspect.checkClubStatus());

            verify(clubService, never()).getClubById(anyString());
        }

        @Test
        @DisplayName("FALLIMENTO: blocco per club non approvato")
        public void fail_clubNotYetApproved() {
            String email = "test@gmail.com";
            String clubId = "clubId";

            ClubManager manager = new ClubManager();
            manager.setId("userId");
            manager.setEmail(email);
            manager.setRole(Role.CLUB_MANAGER);
            manager.setManagedClub(clubId);

            ClubDTO club = new ClubDTO();
            club.setId(clubId);
            club.setAffiliationStatus(AffiliationStatus.SUBMITTED);

            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(email);
            when(userRepository.findByEmail(email)).thenReturn(Optional.of(manager));
            when(clubService.getClubById(clubId)).thenReturn(club);

            assertThrows(UnauthorizedException.class, () -> clubApprovalAspect.checkClubStatus());

            verify(clubService, times(1)).getClubById(clubId);
        }

        @Test
        @DisplayName("SUCCESS: nessun blocco -> club già approvato")
        public void success() {
            String email = "test@gmail.com";
            String clubId = "clubId";

            ClubManager manager = new ClubManager();
            manager.setId("userId");
            manager.setEmail(email);
            manager.setRole(Role.CLUB_MANAGER);
            manager.setManagedClub(clubId);

            ClubDTO club = new ClubDTO();
            club.setId(clubId);
            club.setAffiliationStatus(AffiliationStatus.ACCEPTED);
            club.setFirstAffiliationDate(LocalDate.of(2025,1,1));

            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(email);
            when(userRepository.findByEmail(email)).thenReturn(Optional.of(manager));
            when(clubService.getClubById(clubId)).thenReturn(club);

            clubApprovalAspect.checkClubStatus();

            verify(clubService, times(1)).getClubById(clubId);
        }
    }
}
