package com.backend.roomie.auth;

import com.backend.roomie.config.JwtService;
import com.backend.roomie.dtos.UserDTO;
import com.backend.roomie.models.User;
import com.backend.roomie.models.UserRole;
import com.backend.roomie.repositories.UserRepository;
import com.backend.roomie.repositories.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for handling authentication operations
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Register a new user
     * 
     * @param request registration request
     * @return authentication response with JWT token
     */
    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        // Validate request
        validateRegistrationRequest(request);

        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        // Create new user
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPasswordConfirmation(passwordEncoder.encode(request.getPasswordConfirmation()));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setLocation(request.getLocation());
        user.setAge(request.getAge());
        user.setEmailVerified(false);
        user.setPhoneVerified(false);
        user.setIdVerified(false);
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        user.setRoles(new ArrayList<>());

        // Save user
        User savedUser = userRepository.save(user);

        // Create default role (RENTER)
        UserRole role = new UserRole();
        role.setUser(savedUser);
        role.setRoleType(UserRole.RoleType.RENTER);
        role.setCreatedAt(new Date());
        role.setUpdatedAt(new Date());

        // Save role
        userRoleRepository.save(role);

        // Generate JWT token
        String jwtToken = jwtService.generateToken(
                org.springframework.security.core.userdetails.User.builder()
                        .username(savedUser.getEmail())
                        .password(savedUser.getPassword())
                        .authorities("ROLE_RENTER")
                        .build()
        );

        // Return response
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .roles(List.of("RENTER"))
                .build();
    }

    /**
     * Authenticate a user
     * 
     * @param request authentication request
     * @return authentication response with JWT token
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // Validate request
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }

        // Authenticate user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Get user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        // Get roles
        List<String> roles = user.getRoles().stream()
                .map(role -> role.getRoleType().name())
                .collect(Collectors.toList());

        // Generate JWT token
        String jwtToken = jwtService.generateToken(
                org.springframework.security.core.userdetails.User.builder()
                        .username(user.getEmail())
                        .password(user.getPassword())
                        .authorities(user.getRoles().stream()
                                .map(role -> "ROLE_" + role.getRoleType().name())
                                .collect(Collectors.toList())
                                .toArray(new String[0]))
                        .build()
        );

        // Return response
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(roles)
                .build();
    }

    /**
     * Add role to user
     * 
     * @param userId user ID
     * @param roleType role type
     * @return authentication response with JWT token
     */
    @Transactional
    public AuthenticationResponse addRoleToUser(Long userId, UserRole.RoleType roleType) {
        // Get user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Check if user already has this role
        boolean hasRole = user.getRoles().stream()
                .anyMatch(role -> role.getRoleType() == roleType);

        if (hasRole) {
            throw new IllegalArgumentException("User already has this role");
        }

        // Create new role
        UserRole role = new UserRole();
        role.setUser(user);
        role.setRoleType(roleType);
        role.setCreatedAt(new Date());
        role.setUpdatedAt(new Date());

        // Save role
        userRoleRepository.save(role);

        // Refresh user to get updated roles
        User updatedUser = userRepository.findById(userId).orElseThrow();

        // Get roles
        List<String> roles = updatedUser.getRoles().stream()
                .map(r -> r.getRoleType().name())
                .collect(Collectors.toList());

        // Generate JWT token with updated roles
        String jwtToken = jwtService.generateToken(
                org.springframework.security.core.userdetails.User.builder()
                        .username(updatedUser.getEmail())
                        .password(updatedUser.getPassword())
                        .authorities(updatedUser.getRoles().stream()
                                .map(r -> "ROLE_" + r.getRoleType().name())
                                .collect(Collectors.toList())
                                .toArray(new String[0]))
                        .build()
        );

        // Return response
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .userId(updatedUser.getId())
                .email(updatedUser.getEmail())
                .firstName(updatedUser.getFirstName())
                .lastName(updatedUser.getLastName())
                .roles(roles)
                .build();
    }

    /**
     * Validate registration request
     * 
     * @param request registration request
     * @throws IllegalArgumentException if request is invalid
     */
    private void validateRegistrationRequest(RegisterRequest request) {
        if (request.getFirstName() == null || request.getFirstName().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (request.getLastName() == null || request.getLastName().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (request.getPasswordConfirmation() == null || request.getPasswordConfirmation().isEmpty()) {
            throw new IllegalArgumentException("Password confirmation is required");
        }
        if (!request.getPassword().equals(request.getPasswordConfirmation())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        if (request.getPhoneNumber() == null || request.getPhoneNumber().isEmpty()) {
            throw new IllegalArgumentException("Phone number is required");
        }
        if (request.getLocation() == null || request.getLocation().isEmpty()) {
            throw new IllegalArgumentException("Location is required");
        }
        if (request.getAge() == null) {
            throw new IllegalArgumentException("Age is required");
        }
    }
}
