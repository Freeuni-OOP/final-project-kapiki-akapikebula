package com.kapiki_akapikebula.app.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secretString", "KapikiAkapikebulaSecretKeyForJwtAuthenticationMustBeLongEnough");
    }

    @Test
    void generateToken_ShouldReturnValidToken() {
        String token = jwtUtil.generateToken("test@user.com", "test");
        assertNotNull(token);
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    void getEmailFromToken_ShouldReturnCorrectEmail() {
        String expectedEmail = "test@user.com";
        String token = jwtUtil.generateToken(expectedEmail, "test");
        String extractedEmail = jwtUtil.getEmailFromToken(token);
        assertEquals(expectedEmail, extractedEmail);
    }

    @Test
    void validateToken_ShouldReturnFalse_WhenTokenIsInvalid() {
        assertFalse(jwtUtil.validateToken("fake-or-invalid-token-string"));
    }
}