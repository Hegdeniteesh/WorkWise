package com.workwise.service;

import com.workwise.model.User;
import com.workwise.repository.UserRepository;
import com.workwise.security.JwtUtils;
import com.workwise.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    public String registerUser(User user) throws Exception {
        // Check if user already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new Exception("Email is already registered");
        }

        if (user.getPhoneNumber() != null &&
                userRepository.findByPhoneNumber(user.getPhoneNumber()).isPresent()) {
            throw new Exception("Phone number is already registered");
        }

        // Encrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save user
        User savedUser = userRepository.save(user);

        // Generate JWT token
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(savedUser.getEmail(), user.getPassword())
        );

        return jwtUtils.generateJwtToken(authentication);
    }

    public String loginUser(String email, String password) throws Exception {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtUtils.generateJwtToken(authentication);
    }

    public User getUserFromToken(String token) throws Exception {
        String email = jwtUtils.getUserNameFromJwtToken(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("User not found"));
    }
}
