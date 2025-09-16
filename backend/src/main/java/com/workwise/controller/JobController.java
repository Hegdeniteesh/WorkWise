package com.workwise.controller;

import com.workwise.model.Job;
import com.workwise.model.JobStatus;
import com.workwise.model.SkillCategory;
import com.workwise.model.User;
import com.workwise.repository.JobRepository;
import com.workwise.repository.UserRepository;
import com.workwise.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "http://localhost:3000")
public class JobController {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/create")
    @PreAuthorize("hasRole('HIRER') or hasRole('BOTH')")
    public ResponseEntity<?> createJob(@Valid @RequestBody Job job) {
        try {
            // Get current user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            User hirer = userRepository.findByEmail(userDetails.getEmail())
                    .orElseThrow(() -> new Exception("Hirer not found"));

            // Set job details
            job.setHirer(hirer);
            job.setStatus(JobStatus.POSTED);
            job.setCreatedAt(LocalDateTime.now());
            job.setUpdatedAt(LocalDateTime.now());

            Job savedJob = jobRepository.save(job);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Job created successfully");
            response.put("jobId", savedJob.getId());
            response.put("title", savedJob.getTitle());
            response.put("status", savedJob.getStatus());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Job creation failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Job> jobsPage = jobRepository.findAll(pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("jobs", jobsPage.getContent());
            response.put("currentPage", jobsPage.getNumber());
            response.put("totalItems", jobsPage.getTotalElements());
            response.put("totalPages", jobsPage.getTotalPages());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to fetch jobs: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getJobById(@PathVariable Long id) {
        try {
            Optional<Job> job = jobRepository.findById(id);
            if (job.isPresent()) {
                return ResponseEntity.ok(job.get());
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Job not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch job: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchJobs(
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) SkillCategory category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Boolean isUrgent,
            @RequestParam(required = false) Boolean isAgriculture,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            List<Job> jobs;

            // Build search query based on parameters
            if (isAgriculture != null && isAgriculture) {
                List<SkillCategory> agriculturalCategories = List.of(
                        SkillCategory.FARMING, SkillCategory.CROP_HARVESTING,
                        SkillCategory.LIVESTOCK, SkillCategory.IRRIGATION,
                        SkillCategory.AGRICULTURAL_MACHINERY
                );
                jobs = jobRepository.findAgriculturalJobs(agriculturalCategories);
            } else if (skill != null) {
                jobs = jobRepository.findBySkillRequiredContainingIgnoreCase(skill);
            } else if (city != null) {
                jobs = jobRepository.findByCityIgnoreCase(city);
            } else if (category != null) {
                jobs = jobRepository.findBySkillCategory(category);
            } else {
                jobs = jobRepository.findByStatus(JobStatus.POSTED);
            }

            // Filter by additional criteria
            if (minPrice != null && maxPrice != null) {
                jobs = jobs.stream()
                        .filter(job -> job.getOfferedPrice() != null &&
                                job.getOfferedPrice() >= minPrice &&
                                job.getOfferedPrice() <= maxPrice)
                        .toList();
            }

            if (isUrgent != null && isUrgent) {
                jobs = jobs.stream()
                        .filter(Job::getIsUrgent)
                        .toList();
            }

            Map<String, Object> response = new HashMap<>();
            response.put("jobs", jobs);
            response.put("totalItems", jobs.size());
            response.put("searchCriteria", Map.of(
                    "skill", skill,
                    "city", city,
                    "category", category,
                    "minPrice", minPrice,
                    "maxPrice", maxPrice,
                    "isUrgent", isUrgent,
                    "isAgriculture", isAgriculture
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Search failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/my-jobs")
    @PreAuthorize("hasRole('HIRER') or hasRole('BOTH')")
    public ResponseEntity<List<Job>> getMyJobs() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            List<Job> jobs = jobRepository.findByHirerId(userDetails.getId());
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/assigned-to-me")
    @PreAuthorize("hasRole('WORKER') or hasRole('BOTH')")
    public ResponseEntity<List<Job>> getAssignedJobs() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            List<Job> jobs = jobRepository.findByAssignedWorkerId(userDetails.getId());
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/apply")
    @PreAuthorize("hasRole('WORKER') or hasRole('BOTH')")
    public ResponseEntity<?> applyForJob(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            Optional<Job> jobOptional = jobRepository.findById(id);
            if (jobOptional.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Job not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            Job job = jobOptional.get();

            if (job.getStatus() != JobStatus.POSTED) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Job is not available for application");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            User worker = userRepository.findByEmail(userDetails.getEmail())
                    .orElseThrow(() -> new Exception("Worker not found"));

            job.setAssignedWorker(worker);
            job.setStatus(JobStatus.WORKER_ASSIGNED);
            job.setUpdatedAt(LocalDateTime.now());

            Job updatedJob = jobRepository.save(job);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Successfully applied for job");
            response.put("jobId", updatedJob.getId());
            response.put("status", updatedJob.getStatus());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Application failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('HIRER') or hasRole('BOTH')")
    public ResponseEntity<?> updateJobStatus(@PathVariable Long id, @RequestBody Map<String, String> statusUpdate) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            Optional<Job> jobOptional = jobRepository.findById(id);
            if (jobOptional.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Job not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            Job job = jobOptional.get();

            // Check if user owns this job
            if (!job.getHirer().getEmail().equals(userDetails.getEmail())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Unauthorized to update this job");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }

            JobStatus newStatus = JobStatus.valueOf(statusUpdate.get("status"));
            job.setStatus(newStatus);
            job.setUpdatedAt(LocalDateTime.now());

            if (newStatus == JobStatus.COMPLETED) {
                job.setCompletedAt(LocalDateTime.now());
            }

            Job updatedJob = jobRepository.save(job);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Job status updated successfully");
            response.put("jobId", updatedJob.getId());
            response.put("status", updatedJob.getStatus());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Status update failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('HIRER') or hasRole('BOTH')")
    public ResponseEntity<?> deleteJob(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            Optional<Job> jobOptional = jobRepository.findById(id);
            if (jobOptional.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Job not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            Job job = jobOptional.get();

            // Check if user owns this job
            if (!job.getHirer().getEmail().equals(userDetails.getEmail())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Unauthorized to delete this job");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }

            jobRepository.delete(job);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Job deleted successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Delete failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getJobStats() {
        try {
            long totalJobs = jobRepository.count();
            long postedJobs = jobRepository.findByStatus(JobStatus.POSTED).size();
            long completedJobs = jobRepository.findByStatus(JobStatus.COMPLETED).size();
            long inProgressJobs = jobRepository.findByStatus(JobStatus.IN_PROGRESS).size();

            // Agricultural jobs count
            List<SkillCategory> agriculturalCategories = List.of(
                    SkillCategory.FARMING, SkillCategory.CROP_HARVESTING,
                    SkillCategory.LIVESTOCK, SkillCategory.IRRIGATION,
                    SkillCategory.AGRICULTURAL_MACHINERY
            );
            long agriculturalJobs = jobRepository.findAgriculturalJobs(agriculturalCategories).size();

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalJobs", totalJobs);
            stats.put("postedJobs", postedJobs);
            stats.put("completedJobs", completedJobs);
            stats.put("inProgressJobs", inProgressJobs);
            stats.put("agriculturalJobs", agriculturalJobs);
            stats.put("completionRate", totalJobs > 0 ? (double) completedJobs / totalJobs * 100 : 0);

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch stats: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
