package com.tesi.federazione.backend.security;

import com.tesi.federazione.backend.dto.user.JwtResponseDTO;
import com.tesi.federazione.backend.model.User;
import com.tesi.federazione.backend.model.enums.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {

    @InjectMocks
    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        String SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
        long EXPIRATION = 3600000;
        ReflectionTestUtils.setField(jwtUtils, "secretKey", SECRET);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpiration", EXPIRATION);
    }

    @Test
    @DisplayName("Generate Token: Crea token valido con claim corretti")
    void testGenerateToken() {
        User user = new User();
        user.setId("userId");
        user.setEmail("test@email.com");
        user.setRole(Role.CLUB_MANAGER);

        JwtResponseDTO response = jwtUtils.generateToken(user);

        assertNotNull(response.getToken());
        assertEquals("test@email.com", response.getEmail());
        assertEquals(Role.CLUB_MANAGER, response.getRole());
        assertEquals("test@email.com", jwtUtils.extractUsername(response.getToken()));
    }

    @Test
    @DisplayName("Validate Token: Token valido")
    void validateJwtTokenTest_Success() {
        User user = new User();
        user.setEmail("test@email.com");
        user.setRole(Role.ATHLETE);

        JwtResponseDTO response = jwtUtils.generateToken(user);

        assertTrue(jwtUtils.isTokenValid(response.getToken(), user));
        assertTrue(jwtUtils.validateJwtToken(response.getToken()));
    }

    @Test
    @DisplayName("Validate Token: Token scaduto")
    void validateJwtTokenTest_Expired() {
        ReflectionTestUtils.setField(jwtUtils, "jwtExpiration", -1000L);

        User user = new User();
        user.setEmail("test@email.com");

        JwtResponseDTO response = jwtUtils.generateToken(user);

        assertFalse(jwtUtils.validateJwtToken(response.getToken()));
    }

    @Test
    @DisplayName("Validate Token: Firma invalida")
    void validateJwtTokenTest_InvalidSecretKey() {
        User user = new User();
        user.setEmail("test@email.com");

        String fakeToken = Jwts.builder()
                .setSubject("test@email.com")
                .signWith(Keys.secretKeyFor(SignatureAlgorithm.HS256))
                .compact();

        assertFalse(jwtUtils.validateJwtToken(fakeToken));
    }
}