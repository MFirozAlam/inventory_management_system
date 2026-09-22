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

 public AuthController(AuthService auth) {
  this.auth = auth;
 }

 record LoginRequest(String username, String password) {
 }

 record LoginResponse(
         Integer id,
         String name,
         String username,
         String userType,
         String token
 ) {
 }

 @PostMapping("/register")
 public ResponseEntity<User> register(@RequestBody User user) {
  return ResponseEntity
          .status(HttpStatus.CREATED)
          .body(auth.register(user));
 }

 @PostMapping("/login")
 public LoginResponse login(@RequestBody LoginRequest req) {

  String token = auth.login(
          req.username(),
          req.password()
  );

  User user = auth.userForToken(token)
          .orElseThrow();

  return new LoginResponse(
          user.getId(),
          user.getName(),
          user.getUsername(),
          user.getUserType(),
          token
  );
 }

 @PostMapping("/logout")
 public ResponseEntity<?> logout(
         @RequestHeader(
                 value = "Authorization",
                 required = false
         ) String header) {

  if (header != null && header.startsWith("Bearer ")) {
   auth.logout(header.substring(7));
  }

  return ResponseEntity.ok(
          Map.of("message", "Logged out successfully")
  );
 }
}