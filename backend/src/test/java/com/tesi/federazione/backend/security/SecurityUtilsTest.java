package com.tesi.federazione.backend.security;

import com.tesi.federazione.backend.model.Athlete;
import com.tesi.federazione.backend.model.ClubManager;
import com.tesi.federazione.backend.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityUtilsTest {

    private final SecurityUtils securityUtils = new SecurityUtils();
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

    private void mockUser(User user) {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
    }


    @Test
    @DisplayName("Test per: getCurrentUserId()")
    void getCurrentUserIdTest() {
        String id = "userId";
        User user = new User();
        user.setId(id);

        mockUser(user);

        String result = securityUtils.getCurrentUserId();
        assertEquals(id, result);
    }

    @Test
    @DisplayName("Test per: getCurrentUserEmail()")
    void getCurrentUserEmailTest() {
        String email = "test@gmail.com";
        User user = new User();
        user.setEmail(email);

        mockUser(user);

        String result = securityUtils.getCurrentUserEmail();
        assertEquals(email, result);
    }

    @Test
    @DisplayName("Test per: isMyClub() - ClubManager")
    void isMyClubTest_clubManager() {
        ClubManager manager = new ClubManager();
        manager.setManagedClub("clubId1");

        mockUser(manager);

        assertTrue(securityUtils.isMyClub("clubId1"));
        assertFalse(securityUtils.isMyClub("clubId2"));
    }

    @Test
    @DisplayName("Test per: isMyClub() - Atleta")
    void isMyClubTest_Athlete() {
        Athlete athlete = new Athlete();
        athlete.setClubId("clubId1");

        mockUser(athlete);

        assertTrue(securityUtils.isMyClub("clubId1"));
        assertFalse(securityUtils.isMyClub("clubId2"));
    }

    @Test
    @DisplayName("Test per: getUserClub() - ClubManager")
    void getUserClubTest_clubManager() {
        ClubManager manager = new ClubManager();
        manager.setManagedClub("clubId");

        mockUser(manager);
        assertEquals("clubId", securityUtils.getUserClub());
    }

    @Test
    @DisplayName("Test per: getUserClub() - Atleta")
    void getUserClubTest_athlete() {
        Athlete athlete = new Athlete();
        athlete.setClubId("clubId");

        mockUser(athlete);
        assertEquals("clubId", securityUtils.getUserClub());
    }

    @Test
    @DisplayName("Test per: isAthlete() - TRUE")
    void isAthleteTest_True() {
        User user = mock(User.class);

        doReturn(List.of(new SimpleGrantedAuthority("ATHLETE")))
                .when(user).getAuthorities();

        mockUser(user);

        assertTrue(securityUtils.isAthlete());
    }

    @Test
    @DisplayName("Test per: isAthlete() - FALSE")
    void isAthleteTest_False() {
        User user = mock(User.class);

        doReturn(List.of(new SimpleGrantedAuthority("CLUB_MANAGER")))
                .when(user).getAuthorities();

        mockUser(user);

        assertFalse(securityUtils.isAthlete());
    }

    @Test
    @DisplayName("Test per: isClubManager() - TRUE")
    void isClubManagerTest_True() {
        User user = mock(User.class);

        doReturn(List.of(new SimpleGrantedAuthority("CLUB_MANAGER")))
                .when(user).getAuthorities();

        mockUser(user);

        assertTrue(securityUtils.isClubManager());
    }

    @Test
    @DisplayName("Test per: isClubManager() - FALSE")
    void isClubManagerTest_False() {
        User user = mock(User.class);

        doReturn(List.of(new SimpleGrantedAuthority("FEDERATION_MANAGER")))
                .when(user).getAuthorities();

        mockUser(user);

        assertFalse(securityUtils.isClubManager());
    }

    @Test
    @DisplayName("Test per: isFederationManager() - TRUE")
    void isFederationManagerTest_True() {
        User user = mock(User.class);

        doReturn(List.of(new SimpleGrantedAuthority("FEDERATION_MANAGER")))
                .when(user).getAuthorities();

        mockUser(user);

        assertTrue(securityUtils.isFederationManager());
    }

    @Test
    @DisplayName("Test per: isFederationManager() - FALSE")
    void isFederationManagerTest_False() {
        User user = mock(User.class);

        doReturn(List.of(new SimpleGrantedAuthority("ATHLETE")))
                .when(user).getAuthorities();

        mockUser(user);

        assertFalse(securityUtils.isFederationManager());
    }

}