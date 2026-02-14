package net.giagroup.taskflow_api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import lombok.Getter;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    @Getter
    private long expirationMs;

    private Key key;

    @PostConstruct
    private void init() {
        // ensure secret has sufficient length for HS256 (>= 32 bytes). For dev, pad if necessary.
        byte[] secretBytes = secret.getBytes();
        if (secretBytes.length < 32) {
            StringBuilder sb = new StringBuilder(secret);
            while (sb.toString().getBytes().length < 32) {
                sb.append(secret);
            }
            secret = sb.toString();
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }
}
