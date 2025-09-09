package com.workwise.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.time.LocalDateTime;

@Entity
@Table(name = "skills")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"user"})
@ToString(exclude = {"user"})
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "skill_name", nullable = false, length = 100)
    private String skillName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SkillCategory category;

    @Min(value = 0, message = "Experience years cannot be negative")
    @Column(name = "experience_years")
    private Integer experienceYears = 0;

    @Min(value = 0, message = "Rating cannot be negative")
    @Max(value = 5, message = "Rating cannot be more than 5")
    @Column(precision = 3, scale = 2)
    private Double rating = 0.0;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Pricing Information
    @Column(name = "min_hourly_rate", precision = 10, scale = 2)
    private Double minHourlyRate;

    @Column(name = "max_hourly_rate", precision = 10, scale = 2)
    private Double maxHourlyRate;

    @Column(name = "rate_currency", length = 3)
    private String rateCurrency = "INR";

    // Specialization fields
    @Column(name = "crop_specialization", length = 200)
    private String cropSpecialization;

    @Column(name = "is_seasonal_skill")
    private Boolean isSeasonalSkill = false;

    @Column(name = "certification_details", length = 500)
    private String certificationDetails;

    @Column(name = "is_verified_skill")
    private Boolean isVerifiedSkill = false;

    // Timestamps
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference("user-skills")
    private User user;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
