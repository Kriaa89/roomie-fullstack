package com.backend.roomie.controllers;

import com.backend.roomie.dtos.UserDTO;
import com.backend.roomie.models.PropertyList;
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
 * Controller for renter operations
 */
@RestController
@RequestMapping("/api/renter")
@PreAuthorize("hasRole('RENTER')")
@RequiredArgsConstructor
public class RenterController {

    private final PropertyService propertyService;
    private final UserService userService;

    /**
     * Get all available properties
     * 
     * @return list of available properties
     */
    @GetMapping("/properties")
    public ResponseEntity<List<PropertyList>> getAvailableProperties() {
        return ResponseEntity.ok(propertyService.getAvailableProperties());
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
            PropertyList property = propertyService.getPropertyById(id);
            return ResponseEntity.ok(property);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body("Failed to retrieve property. Error: " + e.getMessage());
        }
    }

    /**
     * Get properties by type
     * 
     * @param type property type
     * @return list of properties
     */
    @GetMapping("/properties/type/{type}")
    public ResponseEntity<List<PropertyList>> getPropertiesByType(@PathVariable String type) {
        return ResponseEntity.ok(propertyService.getPropertiesByType(type));
    }

    /**
     * Get properties by location
     * 
     * @param location property location
     * @return list of properties
     */
    @GetMapping("/properties/location/{location}")
    public ResponseEntity<List<PropertyList>> getPropertiesByLocation(@PathVariable String location) {
        return ResponseEntity.ok(propertyService.getPropertiesByLocation(location));
    }

    /**
     * Get property statistics for renter
     * 
     * @return property statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getPropertyStats() {
        long availableProperties = propertyService.getAvailableProperties().size();

        return ResponseEntity.ok(Map.of(
            "availableProperties", availableProperties
        ));
    }

    /**
     * Submit rental request for a property
     * 
     * @param propertyId property ID
     * @param requestDetails request details
     * @return response
     */
    @PostMapping("/properties/{propertyId}/request")
    public ResponseEntity<?> submitRentalRequest(
            @PathVariable Long propertyId,
            @RequestBody Map<String, Object> requestDetails) {
        try {
            // Validate request details
            if (requestDetails == null || requestDetails.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request details are required");
            }

            // Verify property exists
            PropertyList property = propertyService.getPropertyById(propertyId);

            // Get current user
            UserDTO currentUser = userService.getCurrentUser();

            // Create rental request (in a real implementation, this would be stored in a database)
            // For now, we'll just return a success message with the request details
            return ResponseEntity.ok(Map.of(
                "message", "Rental request submitted successfully",
                "propertyId", propertyId,
                "propertyName", property.getName(),
                "userId", currentUser.getId(),
                "userName", currentUser.getFirstName() + " " + currentUser.getLastName(),
                "requestType", requestDetails.getOrDefault("requestType", "interest"),
                "requestDate", new java.util.Date().toString()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body("Failed to submit rental request. Error: " + e.getMessage());
        }
    }

    /**
     * Update renter profile
     * 
     * @param userDTO user data
     * @return updated user
     */
    @PutMapping("/profile")
    public ResponseEntity<?> updateRenterProfile(@RequestBody UserDTO userDTO) {
        try {
            // Get current user
            UserDTO currentUser = userService.getCurrentUser();

            // Validate required fields
            StringBuilder validationErrors = new StringBuilder();

            if (userDTO.getLocation() == null || userDTO.getLocation().isEmpty()) {
                validationErrors.append("Location is required. ");
            }

            if (userDTO.getCity() == null || userDTO.getCity().isEmpty()) {
                validationErrors.append("City is required. ");
            }

            if (userDTO.getOccupation() == null || userDTO.getOccupation().isEmpty()) {
                validationErrors.append("Occupation is required. ");
            }

            if (userDTO.getBio() == null || userDTO.getBio().isEmpty()) {
                validationErrors.append("Bio is required. ");
            }

            if (userDTO.getBudgetMin() == null || userDTO.getBudgetMin().isEmpty()) {
                validationErrors.append("Minimum budget is required. ");
            }

            if (userDTO.getBudgetMax() == null || userDTO.getBudgetMax().isEmpty()) {
                validationErrors.append("Maximum budget is required. ");
            }

            // Return validation errors if any
            if (validationErrors.length() > 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationErrors.toString());
            }

            // Update user profile
            UserDTO updatedUser = userService.updateUser(currentUser.getId(), userDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
