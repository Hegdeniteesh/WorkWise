package com.workwise.repository;

import com.workwise.model.Job;
import com.workwise.model.JobStatus;
import com.workwise.model.SkillCategory;
import com.workwise.model.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    // Basic finders
    List<Job> findByHirerId(Long hirerId);
    List<Job> findByAssignedWorkerId(Long workerId);
    List<Job> findByStatus(JobStatus status);
    List<Job> findBySkillCategory(SkillCategory category);

    // Search by skills
    List<Job> findBySkillRequiredContainingIgnoreCase(String skill);

    // Location-based queries
    List<Job> findByCityIgnoreCase(String city);
    List<Job> findByStateIgnoreCase(String state);
    List<Job> findByPincode(String pincode);

    // Nearby jobs using Haversine formula
    @Query("SELECT j FROM Job j WHERE j.status = 'POSTED' AND " +
            "(6371 * acos(cos(radians(:lat)) * cos(radians(j.latitude)) * " +
            "cos(radians(j.longitude) - radians(:lng)) + " +
            "sin(radians(:lat)) * sin(radians(j.latitude)))) <= :radius " +
            "ORDER BY (6371 * acos(cos(radians(:lat)) * cos(radians(j.latitude)) * " +
            "cos(radians(j.longitude) - radians(:lng)) + " +
            "sin(radians(:lat)) * sin(radians(j.latitude))))")
    List<Job> findNearbyJobs(@Param("lat") Double latitude,
                             @Param("lng") Double longitude,
                             @Param("radius") Double radiusInKm);

    // Agricultural jobs
    List<Job> findByCropTypeContainingIgnoreCase(String cropType);

    @Query("SELECT j FROM Job j WHERE j.skillCategory IN :agriculturalCategories AND j.status = 'POSTED'")
    List<Job> findAgriculturalJobs(@Param("agriculturalCategories") List<SkillCategory> categories);

    List<Job> findByWeatherDependencyTrue();

    // Time-based queries
    @Query("SELECT j FROM Job j WHERE j.status = 'POSTED' AND j.startDate BETWEEN :now AND :tomorrow")
    List<Job> findUrgentJobs(@Param("now") LocalDateTime now, @Param("tomorrow") LocalDateTime tomorrow);

    @Query("SELECT j FROM Job j WHERE j.startDate >= :start AND j.startDate <= :end")
    List<Job> findJobsByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    List<Job> findByIsUrgentTrueAndStatus(JobStatus status);

    // Payment and pricing
    List<Job> findByPaymentType(PaymentType paymentType);

    @Query("SELECT j FROM Job j WHERE j.offeredPrice BETWEEN :minPrice AND :maxPrice AND j.status = 'POSTED'")
    List<Job> findJobsByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);

    // Active and completed jobs
    @Query("SELECT j FROM Job j WHERE j.status IN ('POSTED', 'APPLICATIONS_RECEIVED', 'IN_PROGRESS')")
    List<Job> findActiveJobs();

    @Query("SELECT j FROM Job j WHERE j.status IN ('COMPLETED', 'PAYMENT_COMPLETED')")
    List<Job> findCompletedJobs();

    // Statistical queries
    @Query("SELECT j.city, COUNT(j) FROM Job j GROUP BY j.city ORDER BY COUNT(j) DESC")
    List<Object[]> findJobCountByCity();

    @Query("SELECT j.skillCategory, COUNT(j), AVG(j.offeredPrice) FROM Job j GROUP BY j.skillCategory")
    List<Object[]> findJobStatsByCategory();

    @Query("SELECT COUNT(j) FROM Job j WHERE j.createdAt >= :since")
    Long countJobsCreatedSince(@Param("since") LocalDateTime since);

    // Advanced matching queries
    @Query("SELECT j FROM Job j WHERE j.status = 'POSTED' AND " +
            "j.skillCategory = :category AND j.city = :city AND " +
            "j.offeredPrice >= :minPrice ORDER BY j.createdAt DESC")
    List<Job> findMatchingJobs(@Param("category") SkillCategory category,
                               @Param("city") String city,
                               @Param("minPrice") Double minPrice);
}
