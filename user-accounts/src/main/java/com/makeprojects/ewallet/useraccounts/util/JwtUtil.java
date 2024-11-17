package com.makeprojects.ewallet.useraccounts.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
    //<editor-fold desc="Member Variables, Constants">
    private static final String SECRET_KEY = "my-secret-key-my-secret-key-my-secret-key";
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    //</editor-fold>

    //<editor-fold desc="Public Methods">
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        claims.put("roles", roles);
        String token = this.createToken(claims, userDetails.getUsername());

        if (token == null) {
            throw new RuntimeException("Generated token is null");
        }

        return token;
    }

    public String extractUserName(String token) {
        return this.extractAllClaims(token).getSubject();
    }

    public boolean isTokenExpired(String token) {
        return this.extractAllClaims(token).getExpiration().before(Date.from(Instant.now()));
    }

    public boolean validateToken(String token, String username) {
        if (this.isTokenExpired(token)) {
            return false;
        }

        String tokenUserName = this.extractUserName(token);
        return username.equals(tokenUserName);
    }

    public List<SimpleGrantedAuthority> getRolesFromToken(String token) {
        Claims claims = this.extractAllClaims(token);
        return ((List<String>) claims.get("roles"))
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
    //</editor-fold>

    //<editor-fold desc="Private Methods">
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(10, ChronoUnit.HOURS)))
                .signWith(key)
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    //</editor-fold>

}
