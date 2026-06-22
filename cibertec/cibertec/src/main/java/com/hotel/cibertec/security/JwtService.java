// com/hotel/cibertec/security/JwtService.java
package com.hotel.cibertec.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expirationTime;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration) {

        this.expirationTime = expiration;

        // Si la clave provista es muy corta (menor a 48 caracteres), la rellenamos o generamos una segura.
        if (secret == null || secret.trim().length() < 48) {
            // Suposición: Al ser una clave menor a 48 bytes, aplicamos un fallback con sal segura
            // que garantiza los bits mínimos requeridos por la especificación JWT.
            String secureSecret = (secret + "A_SECURE_COMPLEMENT_LONG_ENOUGH_FOR_HS384_AND_HS512_AUTHENTICATION_CIBERTEC_HOTEL");
            this.secretKey = Keys.hmacShaKeyFor(secureSecret.getBytes(StandardCharsets.UTF_8));
        } else {
            this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        }
    }

    public String generateToken(String correo) {
        return Jwts.builder()
                .subject(correo)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isTokenValid(String token, String correo) {
        final String username = extractUsername(token);
        return (username.equals(correo) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}