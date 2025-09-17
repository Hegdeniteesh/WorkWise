package com.workwise.service;

import com.workwise.model.*;
import com.workwise.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;

    public Rating submitRating(Rating rating) throws Exception {
        // Validate that the job is completed
        Job job = rating.getJob();
        if (job.getStatus() != JobStatus.COMPLETED) {
            throw new Exception("Can only rate completed jobs");
        }

        // Check if rating already exists
        if (ratingRepository.findByJobIdAndRaterId(job.getId(), rating.getRater().getId()).isPresent()) {
            throw new Exception("You have already rated this job");
        }

        // Validate rating values (1-5)
        if (rating.getRating() < 1 || rating.getRating() > 5) {
            throw new Exception("Rating must be between 1 and 5");
        }

        // Save rating
        Rating savedRating = ratingRepository.save(rating);

        // Update user's trust score
        updateUserTrustScore(rating.getRatedUser().getId());

        return savedRating;
    }

    public Map<String, Object> getUserRatingStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();

        Double avgRating = ratingRepository.getAverageRatingForUser(userId);
        Long totalRatings = ratingRepository.getTotalRatingCount(userId);
        Long recommendations = ratingRepository.getRecommendationCount(userId);

        stats.put("averageRating", avgRating != null ? avgRating : 0.0);
        stats.put("totalRatings", totalRatings);
        stats.put("recommendations", recommendations);

        // Detailed breakdown
        stats.put("workQuality", ratingRepository.getAverageWorkQualityForUser(userId));
        stats.put("communication", ratingRepository.getAverageCommunicationForUser(userId));
        stats.put("punctuality", ratingRepository.getAveragePunctualityForUser(userId));

        // Calculate recommendation percentage
        double recommendationPercentage = totalRatings > 0 ? (recommendations.doubleValue() / totalRatings.doubleValue()) * 100 : 0;
        stats.put("recommendationPercentage", recommendationPercentage);

        return stats;
    }

    public List<Rating> getUserReviews(Long userId) {
        return ratingRepository.findByRatedUserId(userId);
    }

    private void updateUserTrustScore(Long userId) {
        try {
            Double avgRating = ratingRepository.getAverageRatingForUser(userId);
            if (avgRating != null) {
                User user = userRepository.findById(userId).orElse(null);
                if (user != null) {
                    // Simple trust score calculation - can be made more sophisticated
                    double trustScore = Math.min(5.0, avgRating);
                    user.setTrustScore(trustScore);
                    userRepository.save(user);
                }
            }
        } catch (Exception e) {
            // Log error but don't fail the main operation
            System.err.println("Failed to update trust score for user " + userId + ": " + e.getMessage());
        }
    }
}
