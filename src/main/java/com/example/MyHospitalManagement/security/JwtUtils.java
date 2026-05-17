package com.example.MyHospitalManagement.security;

import com.example.MyHospitalManagement.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtUtils {

    @Value("${spring.app.jwtSecretKey}")
    private String jwtSecretKey;

    @Value("${spring.app.jwtExpirationMs}")
    private Long jwtExpirationMs;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecretKey));
    }

    public String generateJwtToken(User user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("userId", user.getId().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key())
                .compact();
    }

    /**
     * Returns the Bearer token string from the Authorization header,
     * or {@code null} if the header is absent or not a Bearer token.
     */
    public String extractTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * Validates the JWT signature, structure, and expiry.
     *
     * @return {@code true} if the token is valid; {@code false} otherwise.
     */
    public boolean validateJwtToken(String jwt) {
        try {
            Jwts.parser()
                    .verifyWith(key())
                    .build()
                    .parseSignedClaims(jwt);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT token is expired: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("JWT token is malformed: {}", e.getMessage());
        } catch (SignatureException e) {
            log.warn("JWT signature validation failed: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty or null: {}", e.getMessage());
        }
        return false;
    }

    public String extractUsernameFromToken(String jwt) {
        Claims claims = Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
        return claims.getSubject();
    }
}
