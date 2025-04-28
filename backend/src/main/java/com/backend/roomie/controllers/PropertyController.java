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
    public ResponseEntity<List<PropretyList>> getAllProperties() {
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
            PropretyList property = propertyService.getPropertyById(id);
            return ResponseEntity.ok(property);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
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
            List<PropretyList> properties = propertyService.getPropertiesByOwner(ownerId);
            return ResponseEntity.ok(properties);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
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
            List<PropretyList> properties = propertyService.getPropertiesByCurrentUser();
            return ResponseEntity.ok(properties);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    /**
     * Get available properties
     * 
     * @return list of available properties
     */
    @GetMapping("/available")
    public ResponseEntity<List<PropretyList>> getAvailableProperties() {
        return ResponseEntity.ok(propertyService.getAvailableProperties());
    }

    /**
     * Get properties by type
     * 
     * @param type property type
     * @return list of properties
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<PropretyList>> getPropertiesByType(@PathVariable String type) {
        return ResponseEntity.ok(propertyService.getPropertiesByType(type));
    }

    /**
     * Get properties by location
     * 
     * @param location property location
     * @return list of properties
     */
    @GetMapping("/location/{location}")
    public ResponseEntity<List<PropretyList>> getPropertiesByLocation(@PathVariable String location) {
        return ResponseEntity.ok(propertyService.getPropertiesByLocation(location));
    }

    /**
     * Create property
     * 
     * @param property property data
     * @return created property
     */
    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
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
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
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
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<?> deleteProperty(@PathVariable Long id) {
        try {
            propertyService.deleteProperty(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
