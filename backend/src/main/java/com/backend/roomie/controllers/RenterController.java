package com.backend.roomie.controllers;

import com.backend.roomie.models.PropretyList;
import com.backend.roomie.services.PropertyService;
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

    /**
     * Get all available properties
     * 
     * @return list of available properties
     */
    @GetMapping("/properties")
    public ResponseEntity<List<PropretyList>> getAvailableProperties() {
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
            PropretyList property = propertyService.getPropertyById(id);
            return ResponseEntity.ok(property);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Get properties by type
     * 
     * @param type property type
     * @return list of properties
     */
    @GetMapping("/properties/type/{type}")
    public ResponseEntity<List<PropretyList>> getPropertiesByType(@PathVariable String type) {
        return ResponseEntity.ok(propertyService.getPropertiesByType(type));
    }

    /**
     * Get properties by location
     * 
     * @param location property location
     * @return list of properties
     */
    @GetMapping("/properties/location/{location}")
    public ResponseEntity<List<PropretyList>> getPropertiesByLocation(@PathVariable String location) {
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
            // Verify property exists
            PropretyList property = propertyService.getPropertyById(propertyId);
            
            // In a real implementation, this would create a rental request
            // For now, we'll just return a success message
            return ResponseEntity.ok(Map.of(
                "message", "Rental request submitted successfully",
                "propertyId", propertyId,
                "propertyName", property.getName()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}