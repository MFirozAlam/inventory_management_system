package com.inventory.controller;

import com.inventory.entity.User;
import com.inventory.repository.UserRepository;
import com.inventory.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    record UserResponse(Integer id, String name, String location, String phone, String username, String userType) {}
    private UserResponse response(User u) { return new UserResponse(u.getId(), u.getName(), u.getLocation(), u.getPhone(), u.getUsername(), auth.normalizeRole(u.getUserType())); }
    private final UserRepository repo;
    private final AuthService auth;
    public UserController(UserRepository repo, AuthService auth) { this.repo = repo; this.auth = auth; }

    private void admin(String header) {
        if (header == null || !header.startsWith("Bearer ") || !auth.isAdmin(header.substring(7))) throw new IllegalArgumentException("Admin access required");
    }

    @GetMapping
    public List<UserResponse> all(@RequestHeader("Authorization") String header) { admin(header); return repo.findAll().stream().map(this::response).toList(); }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> one(@PathVariable Integer id, @RequestHeader("Authorization") String header) {
        admin(header);
        return repo.findById(id).map(u -> ResponseEntity.ok(response(u))).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public UserResponse create(@RequestBody User user, @RequestHeader("Authorization") String header) { admin(header); return response(auth.createByAdmin(user)); }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Integer id, @RequestBody User value, @RequestHeader("Authorization") String header) {
        admin(header);
        User user = repo.findById(id).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();
        if (value.getUsername() == null || value.getUsername().isBlank()) throw new IllegalArgumentException("Username is required");
        repo.findByUsername(value.getUsername()).ifPresent(existing -> { if (!existing.getId().equals(id)) throw new IllegalArgumentException("Username already exists"); });
        user.setName(value.getName());
        user.setLocation(value.getLocation());
        user.setPhone(value.getPhone());
        user.setUsername(value.getUsername());
        user.setUserType(auth.normalizeRole(value.getUserType()));
        if (value.getPassword() != null && !value.getPassword().isBlank()) user.setPassword(auth.encodePassword(value.getPassword()));
        return ResponseEntity.ok(response(repo.save(user)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id, @RequestHeader("Authorization") String header) {
        admin(header);
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
