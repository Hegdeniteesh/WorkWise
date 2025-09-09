package com.workwise.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"skills", "postedJobs", "assignedJobs"})
@ToString(exclude = {"skills", "postedJobs", "assignedJobs", "password"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(nullable = false, length = 100)
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Column(unique = true, nullable = false, length = 150)
    private String email;

    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid phone number format")
    @Column(name = "phone_number", unique = true, length = 20)
    private String phoneNumber;

    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserType userType;

    // Language and Localization
    @Column(name = "preferred_language", length = 20)
    private String preferredLanguage = "ENGLISH";

    // Location Information
    @Column(precision = 10, scale = 8)
    private Double latitude;

    @Column(precision = 11, scale = 8)
    private Double longitude;

    @Column(length = 500)
    private String address;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(length = 10)
    private String pincode;

    // Trust and Verification
    @Column(name = "trust_score", precision = 3, scale = 2)
    private Double trustScore = 0.0;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "is_phone_verified")
    private Boolean isPhoneVerified = false;

    @Column(name = "is_email_verified")
    private Boolean isEmailVerified = false;

    // Worker Specific Fields
    @Column(name = "is_seasonal_worker")
    private Boolean isSeasonalWorker = false;

    @Column(name = "primary_work_location", length = 100)
    private String primaryWorkLocation;

    @Column(name = "availability_status")
    private Boolean availabilityStatus = true;

    // Profile Information
    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    // Timestamps
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "last_active")
    private LocalDateTime lastActive = LocalDateTime.now();

    // Relationships
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference("user-skills")
    private List<Skill> skills = new ArrayList<>();

    @OneToMany(mappedBy = "hirer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("hirer-jobs")
    private List<Job> postedJobs = new ArrayList<>();

    @OneToMany(mappedBy = "assignedWorker", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("worker-jobs")
    private List<Job> assignedJobs = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        lastActive = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Utility methods
    public void addSkill(Skill skill) {
        skills.add(skill);
        skill.setUser(this);
    }

    public void removeSkill(Skill skill) {
        skills.remove(skill);
        skill.setUser(null);
    }
}
