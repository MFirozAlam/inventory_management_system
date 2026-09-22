package com.inventory.controller;
import com.inventory.entity.User; import com.inventory.repository.UserRepository; import com.inventory.service.AuthService;
import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/users")
public class UserController {
 private final UserRepository repo; private final AuthService auth;
 public UserController(UserRepository repo,AuthService auth){this.repo=repo;this.auth=auth;}
 private void admin(String header){if(header==null||!header.startsWith("Bearer "))throw new IllegalArgumentException("Authentication required"); User u=auth.userForToken(header.substring(7)).orElseThrow(()->new IllegalArgumentException("Invalid session")); if(!"ADMIN".equalsIgnoreCase(u.getUserType()))throw new IllegalArgumentException("Admin access required");}
 @GetMapping public List<User> all(@RequestHeader("Authorization") String header){admin(header); return repo.findAll();}
 @GetMapping("/{id}") public ResponseEntity<User> one(@PathVariable Integer id,@RequestHeader("Authorization") String header){admin(header);return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());}
 @PostMapping("/add") public User create(@RequestBody User u,@RequestHeader("Authorization") String header){admin(header);return auth.register(u);}
 @PutMapping("/{id}") public ResponseEntity<User> update(@PathVariable Integer id,@RequestBody User v,@RequestHeader("Authorization") String header){admin(header);if(!repo.existsById(id))return ResponseEntity.notFound().build();User u=repo.findById(id).orElseThrow();u.setName(v.getName());u.setLocation(v.getLocation());u.setPhone(v.getPhone());u.setUsername(v.getUsername());u.setUserType(v.getUserType());if(v.getPassword()!=null&&!v.getPassword().isBlank())u.setPassword(auth.encodePassword(v.getPassword()));return ResponseEntity.ok(repo.save(u));}
 @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Integer id,@RequestHeader("Authorization") String header){admin(header);if(!repo.existsById(id))return ResponseEntity.notFound().build();repo.deleteById(id);return ResponseEntity.noContent().build();}
}
