package com.kapiki_akapikebula.app.controller;

import com.kapiki_akapikebula.app.dto.AuthResponse;
import com.kapiki_akapikebula.app.dto.LoginRequest;
import com.kapiki_akapikebula.app.dto.RegisterRequest;
import com.kapiki_akapikebula.app.dto.UserResponse;
import com.kapiki_akapikebula.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        try {
            UserResponse savedUser = userService.registerUser(request);
            return ResponseEntity.ok(savedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(java.util.Map.of("error", "An unexpected error occurred during registration."));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {
        try {
            String token = userService.loginUser(request);
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (RuntimeException e) {

            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(java.util.Map.of("error", "An unexpected error occurred during login."));
        }
    }



    @GetMapping("/registrationConfirm")
    public ResponseEntity<String> confirmRegistration(@RequestParam("token") String token) {
        try {
            String result = userService.verifyToken(token);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}