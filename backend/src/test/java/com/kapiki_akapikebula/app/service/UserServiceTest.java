package com.kapiki_akapikebula.app.service;

import com.kapiki_akapikebula.app.dto.LoginRequest;
import com.kapiki_akapikebula.app.dto.RegisterRequest;
import com.kapiki_akapikebula.app.dto.UserResponse;
import com.kapiki_akapikebula.app.model.User;
import com.kapiki_akapikebula.app.repository.UserRepository;
import com.kapiki_akapikebula.app.repository.VerificationTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private VerificationTokenRepository tokenRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    @Test
    void registerUser_ShouldReturnUserResponse_WhenSuccessful() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@user.com");
        request.setUsername("testuser");
        request.setPassword("password123");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail(request.getEmail());

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = userService.registerUser(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("test@user.com", response.getEmail());
        verify(emailService, times(1)).sendSimpleMessage(eq("test@user.com"), anyString(), anyString());
    }

    @Test
    void registerUser_ShouldThrowException_WhenEmailExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@user.com");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(new User()));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.registerUser(request));
        assertEquals("A user with this email address already exists!", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginUser_ShouldReturnToken_WhenCredentialsAreValid() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@user.com");
        request.setPassword("password123");

        User existingUser = new User();
        existingUser.setEmail("test@user.com");
        existingUser.setPasswordHash("encodedPassword");
        existingUser.setEnabled(true);

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(request.getPassword(), "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("test@user.com", "test")).thenReturn("mock-jwt-token");

        String token = userService.loginUser(request);

        assertEquals("mock-jwt-token", token);
    }
}