package com.workwise.controller;

import com.workwise.model.User;
import com.workwise.model.UserType;
import com.workwise.model.SkillCategory;
import com.workwise.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // Health check endpoints
    @GetMapping("/hello")
    public ResponseEntity<Map<String, String>> hello() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello from WorkWise Backend!");
        response.put("status", "Backend is running successfully");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("database", "Connected to MySQL");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("application", "WorkWise Backend");
        health.put("version", "1.0.0");
        health.put("database", "MySQL Connected");
        health.put("totalUsers", userRepository.count());
        health.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(health);
    }

    // Basic user operations (for testing before auth is fully implemented)
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
        try {
            System.out.println("Registration request received for: " + user.getEmail());

            // Check if email already exists
            if (userRepository.findByEmail(user.getEmail()).isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Email already registered");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
            }

            // Check if phone number already exists
            if (user.getPhoneNumber() != null &&
                    userRepository.findByPhoneNumber(user.getPhoneNumber()).isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Phone number already registered");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
            }

            // Set default values
            if (user.getPreferredLanguage() == null) {
                user.setPreferredLanguage("ENGLISH");
            }

            User savedUser = userRepository.save(user);

            // Return user without password
            Map<String, Object> response = new HashMap<>();
            response.put("id", savedUser.getId());
            response.put("name", savedUser.getName());
            response.put("email", savedUser.getEmail());
            response.put("userType", savedUser.getUserType());
            response.put("message", "User registered successfully");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            System.err.println("Registration error: " + e.getMessage());
            e.printStackTrace();

            Map<String, String> error = new HashMap<>();
            error.put("error", "Registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @GetMapping("/type/{userType}")
    public ResponseEntity<List<User>> getUsersByType(@PathVariable UserType userType) {
        List<User> users = userRepository.findByUserType(userType);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/workers/skill/{skillName}")
    public ResponseEntity<List<User>> getWorkersBySkill(@PathVariable String skillName) {
        List<User> workers = userRepository.findWorkersBySkill(skillName);
        return ResponseEntity.ok(workers);
    }

    @GetMapping("/workers/category/{category}")
    public ResponseEntity<List<User>> getWorkersByCategory(@PathVariable SkillCategory category) {
        List<User> workers = userRepository.findWorkersBySkillCategory(category);
        return ResponseEntity.ok(workers);
    }

    @GetMapping("/workers/nearby")
    public ResponseEntity<List<User>> getNearbyWorkers(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "10") Double radius) {
        List<User> workers = userRepository.findNearbyWorkers(latitude, longitude, radius);
        return ResponseEntity.ok(workers);
    }

    @GetMapping("/workers/seasonal")
    public ResponseEntity<List<User>> getSeasonalWorkers() {
        List<User> workers = userRepository.findByIsSeasonalWorkerTrueAndUserType(UserType.WORKER);
        return ResponseEntity.ok(workers);
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<User>> getUsersByCity(@PathVariable String city) {
        List<User> users = userRepository.findByCityIgnoreCase(city);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody User userDetails) {
        try {
            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isPresent()) {
                User user = userOptional.get();

                // Update allowed fields
                user.setName(userDetails.getName());
                user.setPhoneNumber(userDetails.getPhoneNumber());
                user.setAddress(userDetails.getAddress());
                user.setLatitude(userDetails.getLatitude());
                user.setLongitude(userDetails.getLongitude());
                user.setCity(userDetails.getCity());
                user.setState(userDetails.getState());
                user.setPincode(userDetails.getPincode());
                user.setBio(userDetails.getBio());
                user.setAvailabilityStatus(userDetails.getAvailabilityStatus());
                user.setPreferredLanguage(userDetails.getPreferredLanguage());

                User updatedUser = userRepository.save(user);
                return ResponseEntity.ok(updatedUser);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Update failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            if (userRepository.existsById(id)) {
                userRepository.deleteById(id);
                Map<String, String> response = new HashMap<>();
                response.put("message", "User deleted successfully");
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Delete failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    // Statistics endpoints
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalWorkers", userRepository.countByUserType(UserType.WORKER));
        stats.put("totalHirers", userRepository.countByUserType(UserType.HIRER));
        stats.put("verifiedUsers", userRepository.findByIsVerifiedTrue().size());
        stats.put("seasonalWorkers", userRepository.findByIsSeasonalWorkerTrueAndUserType(UserType.WORKER).size());
        return ResponseEntity.ok(stats);
    }
}
