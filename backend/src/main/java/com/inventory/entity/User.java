package com.inventory.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Integer id;

 @Column(nullable = false)
 private String name;

 @Column(nullable = false)
 private String location;

 @Column(nullable = false)
 private String phone;

 private String email;
 @Column(nullable = false, unique = true)
 private String username;

 @Column(nullable = false)
 private String password;

 @Column(name = "usertype", nullable = false)
 private String userType;
}