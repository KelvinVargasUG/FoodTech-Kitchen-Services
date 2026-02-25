package com.foodtech.kitchen.infrastructure.security;

import com.foodtech.kitchen.application.ports.out.TokenGenerator;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;

public class JwtTokenGenerator implements TokenGenerator {
    private final String secret;
    private final long expirationSeconds;
    private final Clock clock;

    public JwtTokenGenerator(String secret, long expirationSeconds, Clock clock) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("Secret must not be blank");
        }
        if (expirationSeconds <= 0) {
            throw new IllegalArgumentException("Expiration seconds must be positive");
        }
        if (clock == null) {
            throw new IllegalArgumentException("Clock must not be null");
        }
        this.secret = secret;
        this.expirationSeconds = expirationSeconds;
        this.clock = clock;
    }

    @Override
    public String generateToken(String username) {
        Instant now = clock.instant();
        Instant expiration = now.plusSeconds(expirationSeconds);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }
}
