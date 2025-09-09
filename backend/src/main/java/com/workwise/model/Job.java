package com.workwise.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"hirer", "assignedWorker"})
@ToString(exclude = {"hirer", "assignedWorker"})
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Job title is required")
    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "Skill requirement is mandatory")
    @Column(name = "skill_required", nullable = false, length = 100)
    private String skillRequired;

    @Enumerated(EnumType.STRING)
    @Column(name = "skill_category")
    private SkillCategory skillCategory;

    // Pricing Information
    @Column(name = "offered_price", precision = 10, scale = 2)
    private Double offeredPrice;

    @Column(name = "price_currency", length = 3)
    private String priceCurrency = "INR";

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type")
    private PaymentType paymentType = PaymentType.HOURLY;

    // Timing
    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "estimated_hours")
    private Integer estimatedHours;

    @Column(name = "is_urgent")
    private Boolean isUrgent = false;

    // Location (Required for hyperlocal matching)
    @NotNull(message = "Latitude is required for location-based matching")
    @Column(nullable = false, precision = 10, scale = 8)
    private Double latitude;

    @NotNull(message = "Longitude is required for location-based matching")
    @Column(nullable = false, precision = 11, scale = 8)
    private Double longitude;

    @Column(length = 500)
    private String address;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(length = 10)
    private String pincode;

    // Job Specifications
    @Min(value = 1, message = "At least one worker is required")
    @Column(name = "workers_needed")
    private Integer workersNeeded = 1;

    @Column(columnDefinition = "TEXT")
    private String requirements;

    @Column(name = "tools_provided")
    private Boolean toolsProvided = false;

    @Column(name = "accommodation_provided")
    private Boolean accommodationProvided = false;

    @Column(name = "food_provided")
    private Boolean foodProvided = false;

    // Agricultural Specific Fields
    @Column(name = "crop_type", length = 100)
    private String cropType;

    @Column(name = "area_size", precision = 8, scale = 2)
    private Double areaSize;

    @Column(name = "weather_dependency")
    private Boolean weatherDependency = false;

    // Job Status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status = JobStatus.POSTED;

    @Column(name = "completion_notes", columnDefinition = "TEXT")
    private String completionNotes;

    // Timestamps
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hirer_id", nullable = false)
    @JsonBackReference("hirer-jobs")
    private User hirer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_worker_id")
    @JsonBackReference("worker-jobs")
    private User assignedWorker;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        if (status == JobStatus.COMPLETED && completedAt == null) {
            completedAt = LocalDateTime.now();
        }
    }

    // Utility methods
    public boolean isActive() {
        return status == JobStatus.POSTED || status == JobStatus.APPLICATIONS_RECEIVED || status == JobStatus.IN_PROGRESS;
    }

    public boolean isCompleted() {
        return status == JobStatus.COMPLETED || status == JobStatus.PAYMENT_COMPLETED;
    }
}
