package com.workwise.service;

import com.workwise.model.*;
import com.workwise.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchingService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SkillRepository skillRepository;

    public List<MatchResult> findBestWorkersForJob(Long jobId) {
        Optional<Job> jobOptional = jobRepository.findById(jobId);
        if (jobOptional.isEmpty()) {
            return Collections.emptyList();
        }

        Job job = jobOptional.get();
        List<User> availableWorkers = userRepository.findNearbyWorkers(
                job.getLatitude(),
                job.getLongitude(),
                25.0 // 25km radius
        );

        return availableWorkers.stream()
                .map(worker -> calculateMatch(job, worker))
                .filter(match -> match.getMatchScore() > 0.3) // Minimum 30% match
                .sorted((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()))
                .limit(20)
                .collect(Collectors.toList());
    }

    public List<JobRecommendation> getJobRecommendationsForWorker(Long workerId) {
        Optional<User> workerOptional = userRepository.findById(workerId);
        if (workerOptional.isEmpty() || workerOptional.get().getLatitude() == null) {
            return Collections.emptyList();
        }

        User worker = workerOptional.get();
        List<Skill> workerSkills = skillRepository.findByUserId(workerId);

        if (workerSkills.isEmpty()) {
            return Collections.emptyList();
        }

        List<Job> availableJobs = jobRepository.findNearbyJobs(
                worker.getLatitude(),
                worker.getLongitude(),
                30.0 // 30km radius for job recommendations
        );

        return availableJobs.stream()
                .filter(job -> job.getStatus() == JobStatus.POSTED)
                .map(job -> calculateJobRecommendation(worker, job, workerSkills))
                .filter(rec -> rec.getMatchScore() > 0.4) // Minimum 40% match for recommendations
                .sorted((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()))
                .limit(15)
                .collect(Collectors.toList());
    }

    private MatchResult calculateMatch(Job job, User worker) {
        double totalScore = 0.0;
        Map<String, Double> scoreBreakdown = new HashMap<>();

        // 1. Skill Match (40% weight)
        double skillScore = calculateSkillMatch(job, worker);
        scoreBreakdown.put("skillMatch", skillScore);
        totalScore += skillScore * 0.4;

        // 2. Location Proximity (25% weight)
        double distanceScore = calculateDistanceScore(job, worker);
        scoreBreakdown.put("locationScore", distanceScore);
        totalScore += distanceScore * 0.25;

        // 3. Experience Level (20% weight)
        double experienceScore = calculateExperienceScore(job, worker);
        scoreBreakdown.put("experienceScore", experienceScore);
        totalScore += experienceScore * 0.2;

        // 4. Trust Score (10% weight)
        double trustScore = worker.getTrustScore() / 5.0; // Normalize to 0-1
        scoreBreakdown.put("trustScore", trustScore);
        totalScore += trustScore * 0.1;

        // 5. Availability (5% weight)
        double availabilityScore = worker.getAvailabilityStatus() ? 1.0 : 0.0;
        scoreBreakdown.put("availabilityScore", availabilityScore);
        totalScore += availabilityScore * 0.05;

        return new MatchResult(worker.getId(), worker.getName(), totalScore, scoreBreakdown,
                calculateDistance(job.getLatitude(), job.getLongitude(),
                        worker.getLatitude(), worker.getLongitude()));
    }

    private JobRecommendation calculateJobRecommendation(User worker, Job job, List<Skill> workerSkills) {
        double totalScore = 0.0;
        Map<String, String> reasons = new HashMap<>();

        // Skill compatibility
        double skillMatch = 0.0;
        for (Skill skill : workerSkills) {
            if (job.getSkillRequired().toLowerCase().contains(skill.getSkillName().toLowerCase()) ||
                    (job.getSkillCategory() != null && job.getSkillCategory().equals(skill.getCategory()))) {
                skillMatch = Math.max(skillMatch, 0.9);
                reasons.put("skillMatch", "Your " + skill.getSkillName() + " skill matches this job");
                break;
            }
        }
        totalScore += skillMatch * 0.4;

        // Distance factor
        double distance = calculateDistance(job.getLatitude(), job.getLongitude(),
                worker.getLatitude(), worker.getLongitude());
        double distanceScore = Math.max(0, 1.0 - (distance / 50.0)); // Decay over 50km
        totalScore += distanceScore * 0.3;

        if (distance < 10) {
            reasons.put("proximity", "Job is within 10km of your location");
        }

        // Price attractiveness
        if (job.getOfferedPrice() != null) {
            // Simplified price attractiveness - could be enhanced with market data
            double priceScore = Math.min(1.0, job.getOfferedPrice() / 1000.0); // Normalize against ₹1000
            totalScore += priceScore * 0.2;

            if (job.getOfferedPrice() > 500) {
                reasons.put("goodPay", "Competitive payment offered");
            }
        }

        // Urgency bonus
        if (job.getIsUrgent()) {
            totalScore += 0.1;
            reasons.put("urgent", "Urgent job - quick earning opportunity");
        }

        return new JobRecommendation(job.getId(), job.getTitle(), totalScore, reasons, distance);
    }

    private double calculateSkillMatch(Job job, User worker) {
        List<Skill> workerSkills = skillRepository.findByUserId(worker.getId());

        if (workerSkills.isEmpty()) {
            return 0.0;
        }

        double bestMatch = 0.0;
        for (Skill skill : workerSkills) {
            double match = 0.0;

            // Exact skill name match
            if (job.getSkillRequired().toLowerCase().contains(skill.getSkillName().toLowerCase())) {
                match = 0.9;
            }
            // Category match
            else if (job.getSkillCategory() != null && job.getSkillCategory().equals(skill.getCategory())) {
                match = 0.7;
            }
            // Partial text match
            else if (containsPartialMatch(job.getSkillRequired(), skill.getSkillName())) {
                match = 0.5;
            }

            // Experience bonus
            if (match > 0 && skill.getExperienceYears() != null) {
                double expBonus = Math.min(0.2, skill.getExperienceYears() * 0.02);
                match += expBonus;
            }

            bestMatch = Math.max(bestMatch, match);
        }

        return Math.min(1.0, bestMatch);
    }

    private double calculateDistanceScore(Job job, User worker) {
        if (worker.getLatitude() == null || worker.getLongitude() == null) {
            return 0.0;
        }

        double distance = calculateDistance(job.getLatitude(), job.getLongitude(),
                worker.getLatitude(), worker.getLongitude());

        // Score decreases with distance
        if (distance <= 5) return 1.0;
        if (distance <= 10) return 0.8;
        if (distance <= 20) return 0.6;
        if (distance <= 30) return 0.4;
        if (distance <= 50) return 0.2;
        return 0.0;
    }

    private double calculateExperienceScore(Job job, User worker) {
        List<Skill> relevantSkills = skillRepository.findByUserId(worker.getId()).stream()
                .filter(skill -> job.getSkillRequired().toLowerCase().contains(skill.getSkillName().toLowerCase()) ||
                        (job.getSkillCategory() != null && job.getSkillCategory().equals(skill.getCategory())))
                .collect(Collectors.toList());

        if (relevantSkills.isEmpty()) {
            return 0.0;
        }

        double avgExperience = relevantSkills.stream()
                .mapToInt(skill -> skill.getExperienceYears() != null ? skill.getExperienceYears() : 0)
                .average()
                .orElse(0.0);

        // Normalize experience to 0-1 scale (assuming 10 years is excellent)
        return Math.min(1.0, avgExperience / 10.0);
    }

    private boolean containsPartialMatch(String jobSkill, String workerSkill) {
        String[] jobWords = jobSkill.toLowerCase().split("\\s+");
        String[] workerWords = workerSkill.toLowerCase().split("\\s+");

        for (String jobWord : jobWords) {
            for (String workerWord : workerWords) {
                if (jobWord.contains(workerWord) || workerWord.contains(jobWord)) {
                    return true;
                }
            }
        }
        return false;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371; // Earth's radius in kilometers

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    // Inner classes for response objects
    public static class MatchResult {
        private Long workerId;
        private String workerName;
        private double matchScore;
        private Map<String, Double> scoreBreakdown;
        private double distance;

        public MatchResult(Long workerId, String workerName, double matchScore,
                           Map<String, Double> scoreBreakdown, double distance) {
            this.workerId = workerId;
            this.workerName = workerName;
            this.matchScore = matchScore;
            this.scoreBreakdown = scoreBreakdown;
            this.distance = distance;
        }

        // Getters
        public Long getWorkerId() { return workerId; }
        public String getWorkerName() { return workerName; }
        public double getMatchScore() { return matchScore; }
        public Map<String, Double> getScoreBreakdown() { return scoreBreakdown; }
        public double getDistance() { return distance; }
    }

    public static class JobRecommendation {
        private Long jobId;
        private String jobTitle;
        private double matchScore;
        private Map<String, String> reasons;
        private double distance;

        public JobRecommendation(Long jobId, String jobTitle, double matchScore,
                                 Map<String, String> reasons, double distance) {
            this.jobId = jobId;
            this.jobTitle = jobTitle;
            this.matchScore = matchScore;
            this.reasons = reasons;
            this.distance = distance;
        }

        // Getters
        public Long getJobId() { return jobId; }
        public String getJobTitle() { return jobTitle; }
        public double getMatchScore() { return matchScore; }
        public Map<String, String> getReasons() { return reasons; }
        public double getDistance() { return distance; }
    }
}
