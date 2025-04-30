package com.backend.roomie.controllers;

import com.backend.roomie.dtos.UserDTO;
import com.backend.roomie.models.UserRole;
import com.backend.roomie.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for user operations
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Get current authenticated user
     * 
     * @return current user
     */
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        try {
            UserDTO user = userService.getCurrentUser();
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Get user by ID
     * 
     * @param id user ID
     * @return user
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userService.getCurrentUser().getId() == #id")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            UserDTO user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Get all users
     * 
     * @return list of users
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Update user
     * 
     * @param id user ID
     * @param userDTO user data
     * @return updated user
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userService.getCurrentUser().getId() == #id")
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
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userService.getCurrentUser().getId() == #id")
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
    @PostMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
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
    @DeleteMapping("/{id}/roles/{roleType}")
    @PreAuthorize("hasRole('ADMIN')")
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
     * Change user password
     * 
     * @param id user ID
     * @param request request with passwords
     * @return updated user
     */
    @PostMapping("/{id}/change-password")
    @PreAuthorize("@userService.getCurrentUser().getId() == #id")
    public ResponseEntity<?> changePassword(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String currentPassword = request.get("currentPassword");
            String newPassword = request.get("newPassword");
            String confirmPassword = request.get("confirmPassword");
            
            if (currentPassword == null || newPassword == null || confirmPassword == null) {
                return ResponseEntity.badRequest().body("All password fields are required");
            }
            
            UserDTO updatedUser = userService.changePassword(id, currentPassword, newPassword, confirmPassword);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


}