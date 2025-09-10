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

        // Store raw password before encoding
        String rawPassword = user.getPassword();

        // Encrypt password
        user.setPassword(passwordEncoder.encode(rawPassword));

        // Save user
        User savedUser = userRepository.save(user);

        // Generate token directly without authentication step
        UserDetailsImpl userDetails = UserDetailsImpl.build(savedUser);
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        return jwtUtils.generateJwtToken(authToken);
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
