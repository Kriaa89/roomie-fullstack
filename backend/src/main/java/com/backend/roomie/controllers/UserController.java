package com.backend.roomie.controllers;

import com.backend.roomie.models.User;
import com.backend.roomie.models.UserRole;
import com.backend.roomie.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/landing")
    public ResponseEntity<?> landingPage() {
        return ResponseEntity.ok("Welcome to Roomie!");
    }
    /**
     * Endpoint to register a user
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User registeredUser = userService.registerUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Endpoint to login user
     */

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        Optional<User> loggedInUser = userService.loginUser(user.getEmail(), user.getPassword());
        if (loggedInUser.isPresent()) {
            return ResponseEntity.ok(loggedInUser.get());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
    /**
     * Endpoint to get available role types
     */
    @GetMapping("/roles")
    public ResponseEntity<?> getAvailableRoles() {
        List<UserRole.RoleType> roles = Arrays.asList(UserRole.RoleType.values());
        return ResponseEntity.ok(roles);
    }

    /**
     * Endpoint to allow a user to select a role
     */
    @PostMapping("/{userId}/role")
    public ResponseEntity<?> setUserRole(@PathVariable("userId") Long userId, @RequestParam UserRole.RoleType role) {
        try {
            userService.assignRole(userId, role);
            return ResponseEntity.ok("Role assigned successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Endpoint to update user profile
     * @param userId ID of the user to update
     * @param userUpdates the updated user details
     * @return the updated user
     */
    @PutMapping("/{userId}/profile")
    public ResponseEntity<?> updateUserProfile(
            @PathVariable("userId") Long userId,
            @RequestBody User userUpdates) {
        try {
            // Call service to update user profile
            User updatedUser = userService.updateUserProfile(userId, userUpdates);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            // Return error message if update fails
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Endpoint to upload profile picture
     * @param userId ID of the user
     * @param file the profile picture file
     * @return the updated user
     */
    @PostMapping("/{userId}/profile-picture")
    public ResponseEntity<?> uploadProfilePicture(
            @PathVariable("userId") Long userId,
            @RequestParam("file") MultipartFile file) {
        try {
            // Call service to upload profile picture
            User updatedUser = userService.uploadProfilePicture(userId, file);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            // Return error message if upload fails
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }















}
