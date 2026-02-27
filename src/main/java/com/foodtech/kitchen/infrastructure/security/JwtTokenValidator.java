package com.foodtech.kitchen.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.Date;

public class JwtTokenValidator {
    private final String secret;
    private final Clock clock;

    public JwtTokenValidator(String secret, Clock clock) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("Secret must not be blank");
        }
        if (clock == null) {
            throw new IllegalArgumentException("Clock must not be null");
        }
        this.secret = secret;
        this.clock = clock;
    }

    public String validateAndGetSubject(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token must not be blank");
        }

        try {
            Claims claims = Jwts.parserBuilder()
                    .setClock(() -> Date.from(clock.instant()))
                    .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String subject = claims.getSubject();
            if (subject == null || subject.isBlank()) {
                throw new IllegalArgumentException("Token subject must not be blank");
            }
            return subject;
        } catch (JwtException | IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid token", ex);
        }
    }
}
