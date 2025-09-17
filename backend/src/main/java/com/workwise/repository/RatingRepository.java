package com.workwise.repository;

import com.workwise.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findByRatedUserId(Long userId);
    List<Rating> findByRaterIdOrderByCreatedAtDesc(Long raterId);
    Optional<Rating> findByJobIdAndRaterId(Long jobId, Long raterId);

    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.ratedUser.id = :userId")
    Double getAverageRatingForUser(@Param("userId") Long userId);

    @Query("SELECT AVG(r.workQuality) FROM Rating r WHERE r.ratedUser.id = :userId AND r.workQuality IS NOT NULL")
    Double getAverageWorkQualityForUser(@Param("userId") Long userId);

    @Query("SELECT AVG(r.communication) FROM Rating r WHERE r.ratedUser.id = :userId AND r.communication IS NOT NULL")
    Double getAverageCommunicationForUser(@Param("userId") Long userId);

    @Query("SELECT AVG(r.punctuality) FROM Rating r WHERE r.ratedUser.id = :userId AND r.punctuality IS NOT NULL")
    Double getAveragePunctualityForUser(@Param("userId") Long userId);
}