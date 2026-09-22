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
        if (user.getUsername() == null || user.getUsername().isBlank() || user.getPassword() == null || user.getPassword().isBlank()) throw new IllegalArgumentException("Username and password are required");
        if (users.findByUsername(user.getUsername()).isPresent()) throw new IllegalArgumentException("Username already exists");
        user.setUserType("STAFF");
        user.setPassword(encoder.encode(user.getPassword()));
        return users.save(user);
    }
    public String login(String username, String password) {
        User user = users.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));
        boolean encoded = user.getPassword()!=null && (user.getPassword().startsWith("$2a$") || user.getPassword().startsWith("$2b$") || user.getPassword().startsWith("$2y$"));
        boolean valid = encoded && encoder.matches(password, user.getPassword());
        if (!valid && password.equals(user.getPassword())) { user.setPassword(encoder.encode(password)); users.save(user); valid = true; }
        if (!valid) throw new IllegalArgumentException("Invalid username or password");
        String token = UUID.randomUUID().toString(); tokens.put(token, user.getId()); return token;
    }
    public Optional<User> userForToken(String token) { Integer id = token == null ? null : tokens.get(token); return id == null ? Optional.empty() : users.findById(id); }
    public String encodePassword(String password) { return encoder.encode(password); }
    public void logout(String token) { if (token != null) tokens.remove(token); }
}
