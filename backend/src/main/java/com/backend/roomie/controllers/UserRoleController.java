package com.backend.roomie.controllers;

import com.backend.roomie.models.User;
import com.backend.roomie.models.UserRole;
import com.backend.roomie.repositories.UserRepository;
import com.backend.roomie.repositories.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for handling user role-related HTTP requests
 */
@RestController
@RequestMapping("/api/user-roles")
public class UserRoleController {

    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;

    /**
     * Constructor with dependency injection
     */
    @Autowired
    public UserRoleController(UserRoleRepository userRoleRepository, UserRepository userRepository) {
        this.userRoleRepository = userRoleRepository;
        this.userRepository = userRepository;
    }

    /**
     * Assign a role to a user
     * @param userId ID of the user
     * @param roleRequest the role details
     * @return success message
     */
    @PostMapping("/user/{userId}")
    public ResponseEntity<?> assignRoleToUser(
            @PathVariable("userId") Long userId,
            @RequestBody Map<String, String> roleRequest) {
        try {
            // Get the role type from the request
            String roleTypeStr = roleRequest.get("roleType");
            if (roleTypeStr == null || roleTypeStr.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Role type is required");
            }

            // Convert string to enum
            UserRole.RoleType roleType;
            try {
                roleType = UserRole.RoleType.valueOf(roleTypeStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid role type");
            }

            // Find the user
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            // Create a new user role
            UserRole userRole = new UserRole();
            userRole.setUser(user);
            userRole.setRoleType(roleType);
            userRole.setCreatedAt(new Date());
            userRole.setUpdatedAt(new Date());

            // Save the user role
            userRoleRepository.save(userRole);

            // Return success message
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Role assigned successfully");
            response.put("userId", userId);
            response.put("roleType", roleType);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            // Return error message if assignment fails
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Get all roles for a user
     * @param userId ID of the user
     * @return list of roles assigned to the user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserRoles(@PathVariable("userId") Long userId) {
        try {
            // Find the user
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            // Get the user roles
            return ResponseEntity.ok(userRoleRepository.findByUser(user));
        } catch (Exception e) {
            // Return error message if retrieval fails
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}