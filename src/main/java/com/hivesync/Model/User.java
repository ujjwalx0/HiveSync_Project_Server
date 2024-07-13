package com.hivesync.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "app_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 30)
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private LocalDate dateOfBirth;

    private String profilePictureUrl;

    private String bio;

    private String location;

    private String website;

    private String phoneNumber;

    private LocalDate joinedDate;
    
    private LocalDateTime createdTimestamp = LocalDateTime.now();

    private LocalDateTime updatedTimestamp;

    private LocalDateTime lastLogin;

    private String lastIpAddress;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean accountNonLocked = true;
    
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean accountNonExpired = true;
    
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean credentialsNonExpired = true;

    private boolean enabled = true;
}
