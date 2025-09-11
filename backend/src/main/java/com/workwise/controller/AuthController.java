package com.workwise.controller;

import com.workwise.model.User;
import com.workwise.service.AuthService;
import com.workwise.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private AuthService authService;

    // ✅ Unified register endpoint with debug logs
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            // Debug logs
            System.out.println("Registration request received");
            System.out.println("User email: " + user.getEmail());
            System.out.println("Password is null: " + (user.getPassword() == null));
            System.out.println("Password is empty: " + (user.getPassword() != null && user.getPassword().isEmpty()));

            String token = authService.registerUser(user);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("token", token);
            response.put("userType", user.getUserType());
            response.put("email", user.getEmail());
            response.put("name", user.getName());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            String token = authService.loginUser(loginRequest.getEmail(), loginRequest.getPassword());
            User user = authService.getUserFromToken(token);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("token", token);
            response.put("userType", user.getUserType());
            response.put("email", user.getEmail());
            response.put("name", user.getName());
            response.put("userId", user.getId());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            User user = authService.getUserFromToken("dummy"); // TODO: Replace with real token logic

            Map<String, Object> profile = new HashMap<>();
            profile.put("id", userDetails.getId());
            profile.put("name", userDetails.getName());
            profile.put("email", userDetails.getEmail());
            profile.put("userType", user.getUserType());
            profile.put("city", user.getCity());
            profile.put("state", user.getState());
            profile.put("trustScore", user.getTrustScore());
            profile.put("isVerified", user.getIsVerified());

            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    // Inner class for login request
    public static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
