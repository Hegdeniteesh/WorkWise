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

    // --- Explicit Getters and Setters ---
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public UserType getUserType() { return userType; }
    public void setUserType(UserType userType) { this.userType = userType; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPreferredLanguage() { return preferredLanguage; }
    public void setPreferredLanguage(String preferredLanguage) { this.preferredLanguage = preferredLanguage; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public Boolean getAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(Boolean availabilityStatus) { this.availabilityStatus = availabilityStatus; }

    public Double getTrustScore() { return trustScore; }
    public void setTrustScore(Double trustScore) { this.trustScore = trustScore; }

    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }
}
