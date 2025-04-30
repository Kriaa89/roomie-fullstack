package com.backend.roomie.controllers;

import com.backend.roomie.models.VisitRequests;
import com.backend.roomie.services.VisitRequestsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for visit request operations
 */
@RestController
@RequestMapping("/api/visit-requests")
@RequiredArgsConstructor
public class VisitRequestsController {

    private final VisitRequestsService visitRequestsService;

    /**
     * Get all visit requests
     * 
     * @return list of visit requests
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<VisitRequests>> getAllVisitRequests() {
        return ResponseEntity.ok(visitRequestsService.getAllVisitRequests());
    }

    /**
     * Get visit request by ID
     * 
     * @param id visit request ID
     * @return visit request
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getVisitRequestById(@PathVariable Long id) {
        try {
            VisitRequests visitRequest = visitRequestsService.getVisitRequestById(id);
            return ResponseEntity.ok(visitRequest);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Get visit requests by current user
     * 
     * @return list of visit requests
     */
    @GetMapping("/my-requests")
    public ResponseEntity<?> getMyVisitRequests() {
        try {
            List<VisitRequests> visitRequests = visitRequestsService.getVisitRequestsByCurrentUser();
            return ResponseEntity.ok(visitRequests);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    /**
     * Get visit requests by property
     * 
     * @param propertyId property ID
     * @return list of visit requests
     */
    @GetMapping("/property/{propertyId}")
    @PreAuthorize("hasRole('OWNER') or hasRole('ROOMMATE_HOST')")
    public ResponseEntity<?> getVisitRequestsByProperty(@PathVariable Long propertyId) {
        try {
            List<VisitRequests> visitRequests = visitRequestsService.getVisitRequestsByProperty(propertyId);
            return ResponseEntity.ok(visitRequests);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Create visit request
     * 
     * @param visitRequest visit request data
     * @param propertyId property ID
     * @return created visit request
     */
    @PostMapping("/property/{propertyId}")
    @PreAuthorize("hasRole('RENTER')")
    public ResponseEntity<?> createVisitRequest(@RequestBody VisitRequests visitRequest, @PathVariable Long propertyId) {
        try {
            VisitRequests createdVisitRequest = visitRequestsService.createVisitRequest(visitRequest, propertyId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdVisitRequest);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body("Failed to create visit request. Please try again. Error: " + e.getMessage());
        }
    }

    /**
     * Update visit request status
     * 
     * @param id visit request ID
     * @param status new status
     * @return updated visit request
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('OWNER') or hasRole('ROOMMATE_HOST')")
    public ResponseEntity<?> updateVisitRequestStatus(@PathVariable Long id, @RequestParam VisitRequests.Status status) {
        try {
            VisitRequests updatedVisitRequest = visitRequestsService.updateVisitRequestStatus(id, status);
            return ResponseEntity.ok(updatedVisitRequest);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Delete visit request
     * 
     * @param id visit request ID
     * @return response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVisitRequest(@PathVariable Long id) {
        try {
            visitRequestsService.deleteVisitRequest(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}