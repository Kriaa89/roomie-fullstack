package com.backend.roomie.auth;

import com.backend.roomie.config.JwtService;
import com.backend.roomie.models.User;
import com.backend.roomie.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
// Register a new user
public AuthenticationResponse register(RegisterRequest request) {
    // Check if user already exists
    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
        throw new IllegalArgumentException("Email already in use");
    }

    // Check if password and passwordConfirmation match
    if (!request.getPassword().equals(request.getPasswordConfirmation())) {
        throw new IllegalArgumentException("Password and password confirmation do not match");
    }

    // Create new user with all required fields
    User user = User.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .passwordConfirmation(passwordEncoder.encode(request.getPasswordConfirmation()))
            .phoneNumber(request.getPhoneNumber())
            .location(request.getLocation())
            .emailVerified(false)
            .phoneVerified(false)
            .idVerified(false)
            .createdAt(new Date())
            .updatedAt(new Date())
            .build();

    // Save user to database
    userRepository.save(user);

    // Generate JWT token
    String token = jwtService.generateToken(user);

    // Return authentication response with token
    return AuthenticationResponse.builder()
            .token(token)
            .build();
}

// Authenticate an existing user and generate JWT
public AuthenticationResponse authenticate(AuthenticationRequest request) {
    try {
        // Use AuthenticationManager to authenticate the user
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );

        // If authentication is successful, generate a token
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String token = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .build();
    } catch (Exception e) {
        throw new IllegalArgumentException("Invalid credentials");
    }
}



}
