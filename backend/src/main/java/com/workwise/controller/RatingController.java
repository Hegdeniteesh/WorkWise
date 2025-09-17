package com.workwise.controller;

import com.workwise.model.*;
import com.workwise.repository.*;
import com.workwise.service.RatingService;
import com.workwise.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ratings")
@CrossOrigin(origins = "http://localhost:3000")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/submit")
    public ResponseEntity<?> submitRating(@RequestBody Map<String, Object> ratingData) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            User rater = userRepository.findByEmail(userDetails.getEmail())
                    .orElseThrow(() -> new Exception("User not found"));

            // Extract data
            Long jobId = Long.valueOf(ratingData.get("jobId").toString());
            Long ratedUserId = Long.valueOf(ratingData.get("ratedUserId").toString());
            Double rating = Double.valueOf(ratingData.get("rating").toString());
            String review = (String) ratingData.get("review");

            Job job = jobRepository.findById(jobId)
                    .orElseThrow(() -> new Exception("Job not found"));
            User ratedUser = userRepository.findById(ratedUserId)
                    .orElseThrow(() -> new Exception("Rated user not found"));

            Rating ratingEntity = new Rating();
            ratingEntity.setJob(job);
            ratingEntity.setRater(rater);
            ratingEntity.setRatedUser(ratedUser);
            ratingEntity.setRating(rating);
            ratingEntity.setReview(review);

            // Optional detailed ratings
            if (ratingData.containsKey("workQuality")) {
                ratingEntity.setWorkQuality(Double.valueOf(ratingData.get("workQuality").toString()));
            }
            if (ratingData.containsKey("communication")) {
                ratingEntity.setCommunication(Double.valueOf(ratingData.get("communication").toString()));
            }
            if (ratingData.containsKey("punctuality")) {
                ratingEntity.setPunctuality(Double.valueOf(ratingData.get("punctuality").toString()));
            }
            if (ratingData.containsKey("wouldRecommend")) {
                ratingEntity.setWouldRecommend(Boolean.valueOf(ratingData.get("wouldRecommend").toString()));
            }

            Rating savedRating = ratingService.submitRating(ratingEntity);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Rating submitted successfully");
            response.put("ratingId", savedRating.getId());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // FIX 1 (Consistency): Changed Map type to match the other endpoint's error response.
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<Map<String, Object>> getUserRatingStats(@PathVariable Long userId) {
        try {
            Map<String, Object> stats = ratingService.getUserRatingStats(userId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            // FIX 2 (CRITICAL): Changed Map type to match the method signature ResponseEntity<Map<String, Object>>.
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to get rating stats: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/user/{userId}/reviews")
    public ResponseEntity<List<Rating>> getUserReviews(@PathVariable Long userId) {
        try {
            List<Rating> reviews = ratingService.getUserReviews(userId);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/my-reviews")
    public ResponseEntity<List<Rating>> getMyReviews() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            List<Rating> reviews = ratingService.getUserReviews(userDetails.getId());
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}