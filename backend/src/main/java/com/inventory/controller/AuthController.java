package com.inventory.controller;

import com.inventory.entity.User;
import com.inventory.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService auth;
    public AuthController(AuthService auth) { this.auth = auth; }

    record LoginRequest(String username, String password) {}
    record UserResponse(Integer id, String name, String username, String userType, String token) {}

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody User user) {
        User saved = auth.register(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponse(saved.getId(), saved.getName(), saved.getUsername(), auth.normalizeRole(saved.getUserType()), null));
    }

    @PostMapping("/login")
    public UserResponse login(@RequestBody LoginRequest req) {
        String token = auth.login(req.username(), req.password());
        User user = auth.userForToken(token).orElseThrow(() -> new IllegalArgumentException("Invalid session"));
        return new UserResponse(user.getId(), user.getName(), user.getUsername(), auth.normalizeRole(user.getUserType()), token);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String header) {
        if (header != null && header.startsWith("Bearer ")) auth.logout(header.substring(7));
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
}
