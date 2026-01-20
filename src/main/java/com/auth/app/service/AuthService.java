package com.auth.app.service;

import com.auth.app.entity.User;
import com.auth.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@(.+)$"
    );
    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MAX_USERNAME_LENGTH = 100;
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_PASSWORD_LENGTH = 255;
    private static final int MAX_EMAIL_LENGTH = 255;

    public User signUp(String username, String email, String password) {
        // Validate inputs
        validateUsername(username);
        validateEmail(email);
        validatePassword(password);
        
        if (userRepository.existsByUsername(username.trim())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(email.trim())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = User.builder()
                .username(username.trim())
                .email(email.trim())
                .password(passwordEncoder.encode(password))
                .enabled(true)
                .build();

        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return Optional.empty();
        }
        return userRepository.findByUsername(username.trim());
    }

    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        String trimmed = username.trim();
        if (trimmed.length() < MIN_USERNAME_LENGTH) {
            throw new IllegalArgumentException("Username must be at least " + MIN_USERNAME_LENGTH + " characters");
        }
        if (trimmed.length() > MAX_USERNAME_LENGTH) {
            throw new IllegalArgumentException("Username must not exceed " + MAX_USERNAME_LENGTH + " characters");
        }
    }
    
    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        String trimmed = email.trim();
        if (trimmed.length() > MAX_EMAIL_LENGTH) {
            throw new IllegalArgumentException("Email must not exceed " + MAX_EMAIL_LENGTH + " characters");
        }
        if (!EMAIL_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }
    
    private void validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Password must be at least " + MIN_PASSWORD_LENGTH + " characters");
        }
        if (password.length() > MAX_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Password must not exceed " + MAX_PASSWORD_LENGTH + " characters");
        }
    }
}
