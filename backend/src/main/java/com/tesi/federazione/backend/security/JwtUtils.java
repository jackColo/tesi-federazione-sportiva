package com.tesi.federazione.backend.security;

import com.tesi.federazione.backend.dto.user.JwtResponseDTO;
import com.tesi.federazione.backend.model.User;
import com.tesi.federazione.backend.model.enums.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Componente utility per la gestione dei token JWT che si occupa di
 * generazione, validazione ed estrazione dei dati contenuti nei token.
 */
@Component
@Slf4j
public class JwtUtils {

    /**
     * Variabile per validare i token: valore impostato nel file di configurazione
     */
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    /**
     * Variabile per determinare la scadenza di un token: valore impostato nel file di configurazione
     */
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    /**
     * Metodo per estrarre lo username dal token
     *
     * @param token Token da decodificare
     * @return Username contenuto nel token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Metodo generico per estrarre un singolo claim dal token.
     *
     * @param token          Token JWT
     * @param claimsResolver Funzione per risolvere il claim
     * @param <T>            Classe del claim desiderato
     * @return Il valore del claim estratto
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Metodo per generare un nuovo token per l'utente "userDetails" autenticato.
     *
     * @param userDetails Dettagli dell'utente autenticato
     * @return JwtResponseDTO Risposta con token, id, email e ruolo dell'utente autenticato
     */
    public JwtResponseDTO generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", userDetails.getAuthorities());

        String userId = null;
        Role userRole = null;

        if (userDetails instanceof User customUser) {
            claims.put("id", customUser.getId());
            claims.put("role", customUser.getRole());

            userId = customUser.getId();
            userRole = customUser.getRole();
        }
        String jwtToken = buildToken(claims, userDetails, jwtExpiration);
        return new JwtResponseDTO(
                jwtToken,
                userId,
                userDetails.getUsername(),
                userRole
        );
    }
    /**
     * Metodo che valida il token confrontandolo con l'utente che sta facendo la richiesta
     *
     * @param token Token JWT da validare.
     * @param userDetails Dettagli dell'utente con cui confrontare il token.
     * @return true se il token è valido, false altrimenti.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Metodo che valida il token senza bisogno dello UserDetails.
     * @param authToken Il token da verificare.
     * @return true se il token è integro, false se invalido o scaduto.
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Metodo per costruire fisicamente la stringa JWT impostando claims, subject, data creazione, data scadenza e firma.
     */
    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Metodo che verifica la scadenza del token
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Metodo che estrae la data di scadenza del token
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Metodo per estrarre i claims dal token
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Metodo che recupera la chiave crittografica utilizzata per firmare e verificare i token JWT.
     * @return Oggetto Key da utilizzare per firmare e validare i token
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}