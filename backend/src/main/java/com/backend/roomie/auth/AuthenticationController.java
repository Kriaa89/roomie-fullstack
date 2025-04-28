package com.backend.roomie.auth;

import com.backend.roomie.models.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsible for handling authentication requests.
 * This controller provides endpoints for user registration and login.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    /**
     * Endpoint for registering a new user
     * 
     * @param request The registration request containing user details
     * @return JWT token on successful registration or error message
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            // Call service to register the user
            AuthenticationResponse response = authenticationService.register(request);
            // Return JWT token on success
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // Return error message with BAD_REQUEST status
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Endpoint for user login
     * 
     * @param request The login request containing email and password
     * @return JWT token on successful authentication or error message
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request) {
        try {
            // Call service to authenticate the user
            AuthenticationResponse response = authenticationService.authenticate(request);
            // Return JWT token on success
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // Return error message with BAD_REQUEST status
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Endpoint for selecting a role after registration
     * 
     * @param userId user ID
     * @param request request with role type
     * @return JWT token with updated roles
     */
    @PostMapping("/select-role/{userId}")
    public ResponseEntity<?> selectRole(@PathVariable Long userId, @RequestBody RoleSelectionRequest request) {
        try {
            // Convert role type string to enum
            UserRole.RoleType roleType = UserRole.RoleType.valueOf(request.getRoleType().toUpperCase());

            // Call service to add role to user
            AuthenticationResponse response = authenticationService.addRoleToUser(userId, roleType);

            // Return JWT token with updated roles
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // Return error message with BAD_REQUEST status
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
