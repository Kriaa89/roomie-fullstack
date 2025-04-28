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
 * Controller for roommate host operations
 */
@RestController
@RequestMapping("/api/roommate-host")
@PreAuthorize("hasRole('ROOMMATE_HOST')")
@RequiredArgsConstructor
public class RoommateHostController {

    private final PropertyService propertyService;

    /**
     * Get room listings owned by current user
     * 
     * @return list of room listings
     */
    @GetMapping("/rooms")
    public ResponseEntity<?> getMyRoomListings() {
        try {
            List<PropretyList> properties = propertyService.getPropertiesByCurrentUser();
            return ResponseEntity.ok(properties);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    /**
     * Get room listing by ID (owned by current user)
     * 
     * @param id room listing ID
     * @return room listing
     */
    @GetMapping("/rooms/{id}")
    public ResponseEntity<?> getMyRoomListingById(@PathVariable Long id) {
        try {
            PropretyList property = propertyService.getPropertyById(id);
            
            // Verify ownership
            if (!property.getOwner().getId().equals(propertyService.getPropertiesByCurrentUser().get(0).getOwner().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You don't have permission to view this room listing");
            }
            
            return ResponseEntity.ok(property);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Create room listing
     * 
     * @param property room listing data
     * @return created room listing
     */
    @PostMapping("/rooms")
    public ResponseEntity<?> createRoomListing(@RequestBody PropretyList property) {
        try {
            // Set property type to "ROOM" for roommate listings
            property.setType("ROOM");
            
            PropretyList createdProperty = propertyService.createProperty(property);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProperty);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Update room listing
     * 
     * @param id room listing ID
     * @param propertyDetails room listing details
     * @return updated room listing
     */
    @PutMapping("/rooms/{id}")
    public ResponseEntity<?> updateRoomListing(@PathVariable Long id, @RequestBody Map<String, Object> propertyDetails) {
        try {
            // Ensure type remains "ROOM" if it's being updated
            if (propertyDetails.containsKey("type")) {
                propertyDetails.put("type", "ROOM");
            }
            
            PropretyList updatedProperty = propertyService.updateProperty(id, propertyDetails);
            return ResponseEntity.ok(updatedProperty);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Delete room listing
     * 
     * @param id room listing ID
     * @return response
     */
    @DeleteMapping("/rooms/{id}")
    public ResponseEntity<?> deleteRoomListing(@PathVariable Long id) {
        try {
            propertyService.deleteProperty(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Get room listing statistics
     * 
     * @return room listing statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getRoomListingStats() {
        long totalRoomListings = propertyService.countPropertiesByCurrentUser();
        
        return ResponseEntity.ok(Map.of(
            "totalRoomListings", totalRoomListings
        ));
    }
}