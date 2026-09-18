package com.inventory.service;

import com.inventory.entity.User;
import com.inventory.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {
    private final UserRepository users;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final Map<String, Integer> tokens = new ConcurrentHashMap<>();

    public AuthService(UserRepository users) { this.users = users; }

    public User register(User user) {
        validateCredentials(user.getUsername(), user.getPassword());
        if (users.findByUsername(user.getUsername()).isPresent()) throw new IllegalArgumentException("Username already exists");
        user.setUserType("STAFF");
        user.setPassword(encoder.encode(user.getPassword()));
        return users.save(user);
    }

    public User createByAdmin(User user) {
        validateCredentials(user.getUsername(), user.getPassword());
        if (users.findByUsername(user.getUsername()).isPresent()) throw new IllegalArgumentException("Username already exists");
        user.setUserType(normalizeRole(user.getUserType()));
        user.setPassword(encoder.encode(user.getPassword()));
        return users.save(user);
    }

    public String login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) throw new IllegalArgumentException("Username and password are required");
        User user = users.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));
        String stored = user.getPassword();
        boolean valid = stored != null && (stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$")) && encoder.matches(password, stored);
        if (!valid && password.equals(stored)) {
            user.setPassword(encoder.encode(password));
            valid = true;
        }
        if (!valid) throw new IllegalArgumentException("Invalid username or password");
        user.setUserType(normalizeRole(user.getUserType()));
        users.save(user);
        String token = UUID.randomUUID().toString();
        tokens.put(token, user.getId());
        return token;
    }

    public Optional<User> userForToken(String token) {
        Integer id = token == null ? null : tokens.get(token);
        return id == null ? Optional.empty() : users.findById(id);
    }

    public String encodePassword(String password) {
        if (password == null || password.isBlank()) throw new IllegalArgumentException("Password is required");
        if (password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$")) return password;
        return encoder.encode(password);
    }

    public void logout(String token) { if (token != null) tokens.remove(token); }

    public boolean isAdmin(String token) {
        return userForToken(token).map(u -> "ADMIN".equalsIgnoreCase(normalizeRole(u.getUserType()))).orElse(false);
    }

    public String normalizeRole(String role) {
        if (role == null) return "STAFF";
        String value = role.trim().toUpperCase();
        if (value.equals("ADMIN") || value.equals("ADMINISTRATOR")) return "ADMIN";
        return "STAFF";
    }

    private void validateCredentials(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) throw new IllegalArgumentException("Username and password are required");
    }
}
