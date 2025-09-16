package com.workwise.controller;

import com.workwise.model.Skill;
import com.workwise.model.SkillCategory;
import com.workwise.model.User;
import com.workwise.repository.SkillRepository;
import com.workwise.repository.UserRepository;
import com.workwise.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/skills")
@CrossOrigin(origins = "http://localhost:3000")
public class SkillController {

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/add")
    @PreAuthorize("hasRole('WORKER') or hasRole('BOTH')")
    public ResponseEntity<?> addSkill(@Valid @RequestBody Skill skill) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            User user = userRepository.findByEmail(userDetails.getEmail())
                    .orElseThrow(() -> new Exception("User not found"));

            skill.setUser(user);
            skill.setCreatedAt(LocalDateTime.now());
            skill.setUpdatedAt(LocalDateTime.now());

            Skill savedSkill = skillRepository.save(skill);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Skill added successfully");
            response.put("skillId", savedSkill.getId());
            response.put("skillName", savedSkill.getSkillName());
            response.put("category", savedSkill.getCategory());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to add skill: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/my-skills")
    @PreAuthorize("hasRole('WORKER') or hasRole('BOTH')")
    public ResponseEntity<List<Skill>> getMySkills() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            List<Skill> skills = skillRepository.findByUserId(userDetails.getId());
            return ResponseEntity.ok(skills);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Skill>> getUserSkills(@PathVariable Long userId) {
        List<Skill> skills = skillRepository.findByUserId(userId);
        return ResponseEntity.ok(skills);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Skill>> getSkillsByCategory(@PathVariable SkillCategory category) {
        List<Skill> skills = skillRepository.findByCategory(category);
        return ResponseEntity.ok(skills);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Skill>> searchSkills(@RequestParam String skillName) {
        List<Skill> skills = skillRepository.findBySkillNameContainingIgnoreCase(skillName);
        return ResponseEntity.ok(skills);
    }

    @GetMapping("/seasonal")
    public ResponseEntity<List<Skill>> getSeasonalSkills() {
        List<Skill> skills = skillRepository.findByIsSeasonalSkillTrue();
        return ResponseEntity.ok(skills);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('WORKER') or hasRole('BOTH')")
    public ResponseEntity<?> updateSkill(@PathVariable Long id, @Valid @RequestBody Skill skillDetails) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            Optional<Skill> skillOptional = skillRepository.findById(id);
            if (skillOptional.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Skill not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            Skill skill = skillOptional.get();

            // Check if user owns this skill
            if (!skill.getUser().getEmail().equals(userDetails.getEmail())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Unauthorized to update this skill");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }

            // Update skill details
            skill.setSkillName(skillDetails.getSkillName());
            skill.setCategory(skillDetails.getCategory());
            skill.setExperienceYears(skillDetails.getExperienceYears());
            skill.setDescription(skillDetails.getDescription());
            skill.setMinHourlyRate(skillDetails.getMinHourlyRate());
            skill.setMaxHourlyRate(skillDetails.getMaxHourlyRate());
            skill.setCropSpecialization(skillDetails.getCropSpecialization());
            skill.setIsSeasonalSkill(skillDetails.getIsSeasonalSkill());
            skill.setCertificationDetails(skillDetails.getCertificationDetails());
            skill.setUpdatedAt(LocalDateTime.now());

            Skill updatedSkill = skillRepository.save(skill);
            return ResponseEntity.ok(updatedSkill);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Update failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('WORKER') or hasRole('BOTH')")
    public ResponseEntity<?> deleteSkill(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            Optional<Skill> skillOptional = skillRepository.findById(id);
            if (skillOptional.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Skill not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            Skill skill = skillOptional.get();

            // Check if user owns this skill
            if (!skill.getUser().getEmail().equals(userDetails.getEmail())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Unauthorized to delete this skill");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }

            skillRepository.delete(skill);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Skill deleted successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Delete failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getSkillStats() {
        try {
            long totalSkills = skillRepository.count();
            List<Object[]> skillsByCategory = skillRepository.findSkillStatsByCategory();
            List<Object[]> popularSkills = skillRepository.findMostPopularSkills();
            long verifiedSkills = skillRepository.findByIsVerifiedSkillTrue().size();
            long seasonalSkills = skillRepository.findByIsSeasonalSkillTrue().size();

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalSkills", totalSkills);
            stats.put("skillsByCategory", skillsByCategory);
            stats.put("popularSkills", popularSkills);
            stats.put("verifiedSkills", verifiedSkills);
            stats.put("seasonalSkills", seasonalSkills);

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch stats: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
