package com.backend.roomie.controllers;

import com.backend.roomie.models.PropertyList;
import com.backend.roomie.services.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

/**
 * Controller for property operations
 */
@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;

    /**
     * Get all properties
     * 
     * @return list of properties
     */
    @GetMapping
    public ResponseEntity<List<PropertyList>> getAllProperties() {
        return ResponseEntity.ok(propertyService.getAllProperties());
    }

    /**
     * Get property by ID
     * 
     * @param id property ID
     * @return property
     */
    @GetMapping("/{id}")
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
     * Get properties by owner
     * 
     * @param ownerId owner ID
     * @return list of properties
     */
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<?> getPropertiesByOwner(@PathVariable Long ownerId) {
        try {
            List<PropertyList> properties = propertyService.getPropertiesByOwner(ownerId);
            return ResponseEntity.ok(properties);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body("Failed to retrieve properties. Error: " + e.getMessage());
        }
    }

    /**
     * Get properties by current user
     * 
     * @return list of properties
     */
    @GetMapping("/my-properties")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<?> getMyProperties() {
        try {
            List<PropertyList> properties = propertyService.getPropertiesByCurrentUser();
            return ResponseEntity.ok(properties);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body("Failed to retrieve your properties. Error: " + e.getMessage());
        }
    }

    /**
     * Get available properties
     * 
     * @return list of available properties
     */
    @GetMapping("/available")
    public ResponseEntity<List<PropertyList>> getAvailableProperties() {
        return ResponseEntity.ok(propertyService.getAvailableProperties());
    }

    /**
     * Get properties by type
     * 
     * @param type property type
     * @return list of properties
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<PropertyList>> getPropertiesByType(@PathVariable String type) {
        return ResponseEntity.ok(propertyService.getPropertiesByType(type));
    }

    /**
     * Get properties by location
     * 
     * @param location property location
     * @return list of properties
     */
    @GetMapping("/location/{location}")
    public ResponseEntity<List<PropertyList>> getPropertiesByLocation(@PathVariable String location) {
        return ResponseEntity.ok(propertyService.getPropertiesByLocation(location));
    }

    /**
     * Create property
     * 
     * @param property property data
     * @param bindingResult validation result
     * @return created property
     */
    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<?> createProperty(@Valid @RequestBody PropertyList property, BindingResult bindingResult) {
        // Check for validation errors
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder("Validation errors: ");
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMessage.append(error.getField())
                           .append(" - ")
                           .append(error.getDefaultMessage())
                           .append("; ");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
        }

        try {
            PropertyList createdProperty = propertyService.createProperty(property);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProperty);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body("Failed to create property. Please try again. Error: " + e.getMessage());
        }
    }

    /**
     * Update property
     * 
     * @param id property ID
     * @param propertyDetails property details
     * @return updated property
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<?> updateProperty(@PathVariable Long id, @RequestBody Map<String, Object> propertyDetails) {
        try {
            PropertyList updatedProperty = propertyService.updateProperty(id, propertyDetails);
            return ResponseEntity.ok(updatedProperty);
        } catch (IllegalArgumentException e) {
            // Determine the appropriate status code based on the error message
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("permission")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body("Failed to update property. Error: " + e.getMessage());
        }
    }

    /**
     * Delete property
     * 
     * @param id property ID
     * @return response
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<?> deleteProperty(@PathVariable Long id) {
        try {
            propertyService.deleteProperty(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            // Determine the appropriate status code based on the error message
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("permission")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body("Failed to delete property. Error: " + e.getMessage());
        }
    }

    /**
     * Toggle property availability
     * 
     * @param id property ID
     * @return updated property
     */
    @PutMapping("/{id}/toggle-availability")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<?> togglePropertyAvailability(@PathVariable Long id) {
        try {
            // Let the service handle the toggle operation
            PropertyList updatedProperty = propertyService.togglePropertyAvailability(id);
            return ResponseEntity.ok(updatedProperty);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body("Failed to toggle property availability. Error: " + e.getMessage());
        }
    }
}
