package com.inventory.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "customercode", nullable = false, unique = true)
    private String customerCode;

    @Column(name = "fullname", nullable = false)
    private String fullName;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        syncNameFields();
        if (email == null) email = "";
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
        syncNameFields();
        if (email == null) email = "";
    }

    private void syncNameFields() {
        String value = fullName == null ? "" : fullName.trim();
        if (value.isEmpty()) {
            firstName = "";
            lastName = "";
            return;
        }
        int space = value.indexOf(' ');
        if (space < 0) {
            firstName = value;
            lastName = "";
        } else {
            firstName = value.substring(0, space);
            lastName = value.substring(space + 1).trim();
        }
    }
}
