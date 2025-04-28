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
 * Controller for owner operations
 */
@RestController
@RequestMapping("/api/owner")
@PreAuthorize("hasRole('OWNER')")
@RequiredArgsConstructor
public class OwnerController {

    private final PropertyService propertyService;

    /**
     * Get properties owned by current user
     * 
     * @return list of properties
     */
    @GetMapping("/properties")
    public ResponseEntity<?> getMyProperties() {
        try {
            List<PropretyList> properties = propertyService.getPropertiesByCurrentUser();
            return ResponseEntity.ok(properties);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    /**
     * Get property by ID (owned by current user)
     * 
     * @param id property ID
     * @return property
     */
    @GetMapping("/properties/{id}")
    public ResponseEntity<?> getMyPropertyById(@PathVariable Long id) {
        try {
            PropretyList property = propertyService.getPropertyById(id);

            // Verify ownership
            if (!property.getOwner().getId().equals(propertyService.getPropertiesByCurrentUser().get(0).getOwner().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You don't have permission to view this property");
            }

            return ResponseEntity.ok(property);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Create property
     * 
     * @param property property data
     * @return created property
     */
    @PostMapping("/properties")
    public ResponseEntity<?> createProperty(@RequestBody PropretyList property) {
        try {
            PropretyList createdProperty = propertyService.createProperty(property);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProperty);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Update property
     * 
     * @param id property ID
     * @param propertyDetails property details
     * @return updated property
     */
    @PutMapping("/properties/{id}")
    public ResponseEntity<?> updateProperty(@PathVariable Long id, @RequestBody Map<String, Object> propertyDetails) {
        try {
            PropretyList updatedProperty = propertyService.updateProperty(id, propertyDetails);
            return ResponseEntity.ok(updatedProperty);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
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
            propertyService.deleteProperty(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Get property statistics
     * 
     * @return property statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getPropertyStats() {
        long totalProperties = propertyService.countPropertiesByCurrentUser();

        return ResponseEntity.ok(Map.of(
            "totalProperties", totalProperties
        ));
    }
}
