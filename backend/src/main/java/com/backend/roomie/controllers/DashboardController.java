package com.backend.roomie.controllers;

import com.backend.roomie.dtos.UserDTO;
import com.backend.roomie.services.PropertyService;
import com.backend.roomie.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for dashboard operations
 * Provides role-specific dashboard endpoints
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final UserService userService;
    private final PropertyService propertyService;

    /**
     * Get dashboard for RENTER role
     * 
     * @return dashboard data
     */
    @GetMapping("/renter")
    @PreAuthorize("hasRole('RENTER')")
    public ResponseEntity<Map<String, Object>> getRenterDashboard() {
        UserDTO currentUser = userService.getCurrentUser();

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("user", currentUser);
        dashboard.put("role", "RENTER");
        dashboard.put("message", "Welcome to your Renter Dashboard!");

        // Add renter-specific dashboard data with actual counts
        dashboard.put("availableProperties", propertyService.getAvailableProperties().size());
        dashboard.put("matchedProperties", 0);   // This would require a matching service
        dashboard.put("pendingRequests", 0);     // This would require a request service

        return ResponseEntity.ok(dashboard);
    }

    /**
     * Get dashboard for OWNER role
     * 
     * @return dashboard data
     */
    @GetMapping("/owner")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Map<String, Object>> getOwnerDashboard() {
        UserDTO currentUser = userService.getCurrentUser();

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("user", currentUser);
        dashboard.put("role", "OWNER");
        dashboard.put("message", "Welcome to your Owner Dashboard!");

        // Add owner-specific dashboard data with actual counts
        dashboard.put("listedProperties", propertyService.getPropertiesByCurrentUser().size());
        dashboard.put("interestedRenters", 0);   // This would require a matching service
        dashboard.put("pendingRequests", 0);     // This would require a request service

        return ResponseEntity.ok(dashboard);
    }

    /**
     * Get dashboard for ROOMMATE_HOST role
     * 
     * @return dashboard data
     */
    @GetMapping("/roommate-host")
    @PreAuthorize("hasRole('ROOMMATE_HOST')")
    public ResponseEntity<Map<String, Object>> getRoommateHostDashboard() {
        UserDTO currentUser = userService.getCurrentUser();

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("user", currentUser);
        dashboard.put("role", "ROOMMATE_HOST");
        dashboard.put("message", "Welcome to your Roommate Host Dashboard!");

        // Add roommate-host-specific dashboard data with actual counts
        dashboard.put("listedRooms", propertyService.getPropertiesByCurrentUser().size());
        dashboard.put("potentialRoommates", 0);  // This would require a matching service
        dashboard.put("pendingRequests", 0);     // This would require a request service

        return ResponseEntity.ok(dashboard);
    }

    /**
     * Get dashboard for ADMIN role
     * 
     * @return dashboard data
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAdminDashboard() {
        UserDTO currentUser = userService.getCurrentUser();

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("user", currentUser);
        dashboard.put("role", "ADMIN");
        dashboard.put("message", "Welcome to the Admin Dashboard!");

        // Add admin-specific dashboard data with actual counts
        dashboard.put("totalUsers", userService.getAllUsers().size());
        dashboard.put("totalProperties", propertyService.countAllProperties());
        dashboard.put("pendingVerifications", 0); // This would require a verification service
        dashboard.put("reportedContent", 0);     // This would require a content reporting service

        return ResponseEntity.ok(dashboard);
    }
}
