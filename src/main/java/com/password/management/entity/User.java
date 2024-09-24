package com.password.management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
   
    @Column(unique = true, nullable = false)
    private String id;

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Username is required")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "Username must be in email format")
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank(message = "Password is required")
  @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean accountLocked = false;

    private int failedAttempts;

    @ElementCollection
    private List<String> previousPasswords = new ArrayList<>();

    @Column(nullable = false)
    private LocalDate passwordExpirationDate;
    

    @Column(nullable = true)
    private String resetToken;
}
