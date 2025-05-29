package com.jobrecommendation.userservice.service;

import com.jobrecommendation.userservice.model.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String TEST_SECRET = "testSecretKeyThatIsAtLeast256BitsLongForHMACSHA256Algorithm";
    private static final long TEST_EXPIRATION = 3600000; // 1 hour
    
    private JwtService jwtService;
    private User testUser;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtService, "expirationTime", TEST_EXPIRATION);
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@test.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setRoles(Collections.singleton("USER"));
    }

    @Test
    void generateToken_Success() {
        String token = jwtService.generateToken(testUser);
        
        assertNotNull(token);
        assertTrue(token.length() > 0);
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length); // Header, payload, signature
        
        Claims claims = jwtService.getClaimsFromToken(token);
        assertEquals(testUser.getId().toString(), claims.getSubject());
        assertEquals(testUser.getEmail(), claims.get("email"));
        assertTrue(claims.getExpiration().after(new Date()));
    }

    @Test
    void validateToken_Success() {
        String token = jwtService.generateToken(testUser);
        boolean isValid = jwtService.validateToken(token);
        
        assertTrue(isValid);
    }

    @Test
    void validateToken_Failure_InvalidToken() {
        boolean isValid = jwtService.validateToken("invalid.token.here");
        assertFalse(isValid);
    }

    @Test
    void validateToken_Failure_ExpiredToken() {
        // Set a very short expiration for this test
        ReflectionTestUtils.setField(jwtService, "expirationTime", 1L);
        String token = jwtService.generateToken(testUser);
        
        // Wait for token to expire
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        boolean isValid = jwtService.validateToken(token);
        assertFalse(isValid);
        
        // Reset expiration time
        ReflectionTestUtils.setField(jwtService, "expirationTime", TEST_EXPIRATION);
    }

    @Test
    void extractUsername_Success() {
        String token = jwtService.generateToken(testUser);
        Claims claims = jwtService.getClaimsFromToken(token);
        
        assertEquals(testUser.getEmail(), claims.get("email"));
    }

    @Test
    void extractRoles_Success() {
        String token = jwtService.generateToken(testUser);
        Claims claims = jwtService.getClaimsFromToken(token);
        
        @SuppressWarnings("unchecked")
        Collection<String> roles = (Collection<String>) claims.get("roles");
        assertNotNull(roles);
        assertTrue(roles.contains("USER"));
    }

    @Test
    void extractUsername_Failure_InvalidToken() {
        assertThrows(Exception.class, () -> 
            jwtService.getClaimsFromToken("invalid.token.here")
        );
    }
}
