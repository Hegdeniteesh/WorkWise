package com.workwise.controller;

import com.workwise.service.MatchingService;
import com.workwise.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/matching")
@CrossOrigin(origins = "http://localhost:3000")
public class MatchingController {

    @Autowired
    private MatchingService matchingService;

    @GetMapping("/job/{jobId}/workers")
    public ResponseEntity<?> findBestWorkersForJob(@PathVariable Long jobId) {
        try {
            List<MatchingService.MatchResult> matches = matchingService.findBestWorkersForJob(jobId);

            Map<String, Object> response = new HashMap<>();
            response.put("jobId", jobId);
            response.put("matches", matches);
            response.put("totalMatches", matches.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to find matching workers: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/recommendations")
    public ResponseEntity<?> getJobRecommendations() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            List<MatchingService.JobRecommendation> recommendations =
                    matchingService.getJobRecommendationsForWorker(userDetails.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("recommendations", recommendations);
            response.put("totalRecommendations", recommendations.size());
            response.put("message", recommendations.isEmpty() ?
                    "No recommendations found. Try updating your skills and location." :
                    "Found " + recommendations.size() + " job recommendations for you!");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get recommendations: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/worker/{workerId}/recommendations")
    public ResponseEntity<?> getJobRecommendationsForWorker(@PathVariable Long workerId) {
        try {
            List<MatchingService.JobRecommendation> recommendations =
                    matchingService.getJobRecommendationsForWorker(workerId);

            Map<String, Object> response = new HashMap<>();
            response.put("workerId", workerId);
            response.put("recommendations", recommendations);
            response.put("totalRecommendations", recommendations.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get recommendations: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
