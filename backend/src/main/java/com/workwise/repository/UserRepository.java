package com.workwise.repository;

import com.workwise.model.User;
import com.workwise.model.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Basic finders
    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);

    List<User> findByUserType(UserType userType);

    // Location-based queries
    List<User> findByCityIgnoreCase(String city);
    List<User> findByStateIgnoreCase(String state);
    List<User> findByPincode(String pincode);

    // Worker-specific queries
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.skills s WHERE u.userType IN ('WORKER', 'BOTH') AND LOWER(s.skillName) LIKE LOWER(CONCAT('%', :skillName, '%'))")
    List<User> findWorkersBySkill(@Param("skillName") String skillName);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.skills s WHERE u.userType IN ('WORKER', 'BOTH') AND s.category = :category")
    List<User> findWorkersBySkillCategory(@Param("category") com.workwise.model.SkillCategory category);

    // Nearby workers using Haversine formula
    @Query("SELECT u FROM User u WHERE u.userType IN ('WORKER', 'BOTH') AND " +
            "u.latitude IS NOT NULL AND u.longitude IS NOT NULL AND u.availabilityStatus = true AND " +
            "(6371 * acos(cos(radians(:lat)) * cos(radians(u.latitude)) * " +
            "cos(radians(u.longitude) - radians(:lng)) + " +
            "sin(radians(:lat)) * sin(radians(u.latitude)))) <= :radius " +
            "ORDER BY (6371 * acos(cos(radians(:lat)) * cos(radians(u.latitude)) * " +
            "cos(radians(u.longitude) - radians(:lng)) + " +
            "sin(radians(:lat)) * sin(radians(u.latitude))))")
    List<User> findNearbyWorkers(@Param("lat") Double latitude,
                                 @Param("lng") Double longitude,
                                 @Param("radius") Double radiusInKm);

    // Agricultural and seasonal workers
    List<User> findByIsSeasonalWorkerTrueAndUserType(UserType userType);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.skills s WHERE u.isSeasonalWorker = true AND s.isSeasonalSkill = true")
    List<User> findSeasonalWorkersWithSeasonalSkills();

    // Trust and verification
    List<User> findByIsVerifiedTrue();
    List<User> findByTrustScoreGreaterThanEqual(Double minTrustScore);

    // Activity-based queries
    @Query("SELECT u FROM User u WHERE u.lastActive >= :since AND u.userType IN ('WORKER', 'BOTH')")
    List<User> findActiveWorkersSince(@Param("since") LocalDateTime since);

    List<User> findByAvailabilityStatusTrue();

    // Statistical queries
    @Query("SELECT COUNT(u) FROM User u WHERE u.userType = :userType")
    Long countByUserType(@Param("userType") UserType userType);

    @Query("SELECT u.city, COUNT(u) FROM User u WHERE u.userType IN ('WORKER', 'BOTH') GROUP BY u.city ORDER BY COUNT(u) DESC")
    List<Object[]> findWorkerCountByCity();
}
