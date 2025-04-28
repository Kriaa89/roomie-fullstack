package com.backend.roomie.controllers;

import com.backend.roomie.dtos.UserDTO;
import com.backend.roomie.models.PropretyList;
import com.backend.roomie.models.UserRole;
import com.backend.roomie.services.PropertyService;
import com.backend.roomie.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for admin operations
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final PropertyService propertyService;

    /**
     * Get all users
     * 
     * @return list of users
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Get user by ID
     * 
     * @param id user ID
     * @return user
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            UserDTO user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Update user
     * 
     * @param id user ID
     * @param userDTO user data
     * @return updated user
     */
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        try {
            UserDTO updatedUser = userService.updateUser(id, userDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Delete user
     * 
     * @param id user ID
     * @return response
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Add role to user
     * 
     * @param id user ID
     * @param request request with role type
     * @return updated user
     */
    @PostMapping("/users/{id}/roles")
    public ResponseEntity<?> addRoleToUser(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String roleTypeStr = request.get("roleType");
            if (roleTypeStr == null) {
                return ResponseEntity.badRequest().body("Role type is required");
            }

            UserRole.RoleType roleType = UserRole.RoleType.valueOf(roleTypeStr.toUpperCase());
            UserDTO updatedUser = userService.addRoleToUser(id, roleType);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Remove role from user
     * 
     * @param id user ID
     * @param roleType role type
     * @return updated user
     */
    @DeleteMapping("/users/{id}/roles/{roleType}")
    public ResponseEntity<?> removeRoleFromUser(@PathVariable Long id, @PathVariable String roleType) {
        try {
            UserRole.RoleType type = UserRole.RoleType.valueOf(roleType.toUpperCase());
            UserDTO updatedUser = userService.removeRoleFromUser(id, type);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Get all properties
     * 
     * @return list of properties
     */
    @GetMapping("/properties")
    public ResponseEntity<List<PropretyList>> getAllProperties() {
        return ResponseEntity.ok(propertyService.getAllProperties());
    }

    /**
     * Get property by ID
     * 
     * @param id property ID
     * @return property
     */
    @GetMapping("/properties/{id}")
    public ResponseEntity<?> getPropertyById(@PathVariable Long id) {
        try {
            PropretyList property = propertyService.getPropertyById(id);
            return ResponseEntity.ok(property);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Delete property
     * 
     * @param id property ID
     * @return response
     */
    @DeleteMapping("/properties/{id}")
    public ResponseEntity<?> deleteProperty(@PathVariable Long id) {
        try {
            // Admin can delete any property without ownership check
            // First verify the property exists
            propertyService.getPropertyById(id);
            // Then delete it using the service method
            propertyService.deleteProperty(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Get admin dashboard statistics
     * 
     * @return admin dashboard statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getAdminStats() {
        long totalUsers = userService.getAllUsers().size();
        long totalProperties = propertyService.countAllProperties();

        return ResponseEntity.ok(Map.of(
            "totalUsers", totalUsers,
            "totalProperties", totalProperties
        ));
    }
}
