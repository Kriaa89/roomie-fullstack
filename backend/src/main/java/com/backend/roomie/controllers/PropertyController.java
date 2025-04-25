package com.backend.roomie.controllers;

import com.backend.roomie.models.PropretyList;
import com.backend.roomie.services.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * Controller for handling property-related HTTP requests
 */
@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    // Service for property-related operations
    private final PropertyService propertyService;

    /**
     * Constructor with dependency injection
     */
    @Autowired
    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    /**
     * Create a new property listing
     * @param userId ID of the user who owns the property
     * @param property the property details
     * @return the created property
     */
    @PostMapping("/user/{userId}")
    public ResponseEntity<?> createProperty(
            @PathVariable("userId") Long userId,
            @RequestBody PropretyList property) {
        try {
            // Call service to create property
            PropretyList createdProperty = propertyService.createProperty(userId, property);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProperty);
        } catch (Exception e) {
            // Return error message if creation fails
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Get all properties for a user
     * @param userId ID of the user
     * @return list of properties owned by the user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserProperties(@PathVariable("userId") Long userId) {
        try {
            // Call service to get user properties
            List<PropretyList> properties = propertyService.getUserProperties(userId);
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            // Return error message if retrieval fails
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Get a property by ID
     * @param propertyId ID of the property
     * @return the property
     */
    @GetMapping("/{propertyId}")
    public ResponseEntity<?> getProperty(@PathVariable("propertyId") Long propertyId) {
        try {
            // Call service to get property by ID
            PropretyList property = propertyService.getPropertyById(propertyId);
            return ResponseEntity.ok(property);
        } catch (Exception e) {
            // Return error message if property not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Update a property
     * @param propertyId ID of the property to update
     * @param propertyUpdates the updated property details
     * @return the updated property
     */
    @PutMapping("/{propertyId}")
    public ResponseEntity<?> updateProperty(
            @PathVariable("propertyId") Long propertyId,
            @RequestBody PropretyList propertyUpdates) {
        try {
            // Call service to update property
            PropretyList updatedProperty = propertyService.updateProperty(propertyId, propertyUpdates);
            return ResponseEntity.ok(updatedProperty);
        } catch (Exception e) {
            // Return error message if update fails
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Delete a property
     * @param propertyId ID of the property to delete
     * @return success message
     */
    @DeleteMapping("/{propertyId}")
    public ResponseEntity<?> deleteProperty(@PathVariable("propertyId") Long propertyId) {
        try {
            // Call service to delete property
            propertyService.deleteProperty(propertyId);
            return ResponseEntity.ok(Map.of("message", "Property deleted successfully"));
        } catch (Exception e) {
            // Return error message if deletion fails
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Upload property image
     * @param propertyId ID of the property
     * @param file the image file to upload
     * @return URL of the uploaded image
     */
    @PostMapping("/{propertyId}/image")
    public ResponseEntity<?> uploadPropertyImage(
            @PathVariable("propertyId") Long propertyId,
            @RequestParam("file") MultipartFile file) {
        try {
            // Call service to upload property image
            String imageUrl = propertyService.uploadPropertyImage(propertyId, file);
            return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
        } catch (Exception e) {
            // Return error message if upload fails
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}