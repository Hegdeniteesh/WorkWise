package com.workwise.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ratings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rater_id", nullable = false)
    private User rater; // Person giving the rating

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rated_user_id", nullable = false)
    private User ratedUser; // Person being rated

    @Column(nullable = false)
    private Double rating; // 1-5 stars

    @Column(columnDefinition = "TEXT")
    private String review;

    @Column(name = "work_quality")
    private Double workQuality;

    @Column(name = "communication")
    private Double communication;

    @Column(name = "punctuality")
    private Double punctuality;

    @Column(name = "reliability")
    private Double reliability;

    @Column(name = "would_recommend")
    private Boolean wouldRecommend;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
