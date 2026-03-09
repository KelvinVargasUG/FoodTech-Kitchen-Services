package com.foodtech.kitchen.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Date;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag("component")
class JwtTokenGeneratorTest {

    @Test
    void generateToken_includesSubjectAndExpiration_deterministic() {
        String secret = "test-secret-should-be-long-enough-for-hs256-123456";
        long expirationSeconds = 3600L;
        Instant fixedInstant = Instant.parse("2025-01-01T00:00:00Z");
        Clock clock = Clock.fixed(fixedInstant, ZoneOffset.UTC);
        JwtTokenGenerator generator = new JwtTokenGenerator(secret, expirationSeconds, clock);

        String token = generator.generateToken("alice");

        assertNotNull(token);
        assertFalse(token.isBlank());

        Claims claims = Jwts.parserBuilder()
            .setClock(() -> Date.from(fixedInstant))
            .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
            .build()
            .parseClaimsJws(token)
            .getBody();

        assertEquals("alice", claims.getSubject());
        assertEquals(Date.from(fixedInstant), claims.getIssuedAt());
        assertEquals(Date.from(fixedInstant.plusSeconds(expirationSeconds)), claims.getExpiration());
    }

    @Test
    void generateToken_whenSecretIsBlank_throwsIllegalArgumentException() {
        Clock clock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneOffset.UTC);

        assertThrows(IllegalArgumentException.class,
                () -> new JwtTokenGenerator("   ", 3600, clock));
    }

    @Test
    void generateToken_whenExpirationSecondsIsNonPositive_throwsIllegalArgumentException() {
        String secret = "test-secret-should-be-long-enough-for-hs256-123456";
        Clock clock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneOffset.UTC);

        assertThrows(IllegalArgumentException.class,
                () -> new JwtTokenGenerator(secret, 0, clock));
        assertThrows(IllegalArgumentException.class,
                () -> new JwtTokenGenerator(secret, -1, clock));
    }
}
