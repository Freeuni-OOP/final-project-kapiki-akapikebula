package com.kapiki_akapikebula.app.service;

import com.kapiki_akapikebula.app.dto.ChangePasswordRequest;
import com.kapiki_akapikebula.app.dto.LoginRequest;
import com.kapiki_akapikebula.app.dto.RegisterRequest;
import com.kapiki_akapikebula.app.dto.UserResponse;
import com.kapiki_akapikebula.app.model.User;
import com.kapiki_akapikebula.app.model.VerificationToken;
import com.kapiki_akapikebula.app.repository.UserRepository;
import com.kapiki_akapikebula.app.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse registerUser(RegisterRequest request) {
        Optional<User> existingUserOpt = userRepository.findByEmail(request.getEmail());

        User user;

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();

            if (existingUser.isEnabled()) {
                throw new RuntimeException("A user with this email address already exists!");
            }

            user = existingUser;
            user.setUsername(request.getUsername());
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        } else {
            user = new User();
            user.setEmail(request.getEmail());
            user.setUsername(request.getUsername());
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            user.setCreatedAt(LocalDateTime.now());
            user.setEnabled(false);
        }

        User savedUser = userRepository.save(user);

        // Generate and save verification token
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, savedUser);
        tokenRepository.save(verificationToken);

        String confirmationUrl = "http://localhost:8080/api/auth/registrationConfirm?token=" + token;
        String emailBody = "Thank you for registering! Please click the link below to activate your account:\n" + confirmationUrl;

        emailService.sendSimpleMessage(savedUser.getEmail(), "Confirm your Registration", emailBody);

        return new UserResponse(savedUser.getId(), savedUser.getEmail(), savedUser.getCreatedAt());
    }

    public String loginUser(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("A user with this email not found!"));

        if (!user.isEnabled()) {
            throw new RuntimeException("Please verify your email address before logging in.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("incorrect password!");
        }

        return jwtUtil.generateToken(user.getEmail());
    }

    @Transactional
    public String verifyToken(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid verification token!"));

        if (verificationToken.isExpired()) {
            throw new RuntimeException("Verification token has expired!");
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        tokenRepository.delete(verificationToken);

        return "Account verified successfully! You can now log in.";
    }

    public void changePassword(String email, ChangePasswordRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Incorrect old password!");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPasswordHash())) {
            throw new RuntimeException("New password cannot be the same as the old password!");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void updateUsername(String email, String newUsername) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        user.setUsername(newUsername);
        userRepository.save(user);
    }

    @Transactional
    public void deleteAccount(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        user.setEnabled(false);
        userRepository.save(user);
    }
}