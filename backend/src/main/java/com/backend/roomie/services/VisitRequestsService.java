package com.backend.roomie.services;

import com.backend.roomie.models.VisitRequests;
import com.backend.roomie.models.User;
import com.backend.roomie.models.PropertyList;
import com.backend.roomie.repositories.VisitRequestsRepository;
import com.backend.roomie.repositories.UserRepository;
import com.backend.roomie.repositories.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Service for VisitRequests operations
 */
@Service
@RequiredArgsConstructor
public class VisitRequestsService {

    private final VisitRequestsRepository visitRequestsRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;

    /**
     * Get all visit requests
     * 
     * @return list of visit requests
     */
    public List<VisitRequests> getAllVisitRequests() {
        return visitRequestsRepository.findAll();
    }

    /**
     * Get visit request by ID
     * 
     * @param id visit request ID
     * @return visit request
     */
    public VisitRequests getVisitRequestById(Long id) {
        return visitRequestsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Visit request not found"));
    }

    /**
     * Get visit requests by user
     * 
     * @param userId user ID
     * @return list of visit requests
     */
    public List<VisitRequests> getVisitRequestsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return visitRequestsRepository.findByUser(user);
    }

    /**
     * Get visit requests by property
     * 
     * @param propertyId property ID
     * @return list of visit requests
     */
    public List<VisitRequests> getVisitRequestsByProperty(Long propertyId) {
        PropertyList property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Property not found"));
        return visitRequestsRepository.findByPropertyList(property);
    }

    /**
     * Get visit requests by current user
     * 
     * @return list of visit requests
     */
    public List<VisitRequests> getVisitRequestsByCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return visitRequestsRepository.findByUser(user);
    }

    /**
     * Create visit request
     * 
     * @param visitRequest visit request data
     * @param propertyId property ID
     * @return created visit request
     */
    @Transactional
    public VisitRequests createVisitRequest(VisitRequests visitRequest, Long propertyId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        PropertyList property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Property not found"));

        visitRequest.setUser(user);
        visitRequest.setPropertyList(property);
        visitRequest.setStatus(VisitRequests.Status.PENDING);
        visitRequest.setCreatedAt(new Date());
        visitRequest.setUpdatedAt(new Date());

        return visitRequestsRepository.save(visitRequest);
    }

    /**
     * Update visit request status
     * 
     * @param id visit request ID
     * @param status new status
     * @return updated visit request
     */
    @Transactional
    public VisitRequests updateVisitRequestStatus(Long id, VisitRequests.Status status) {
        VisitRequests visitRequest = visitRequestsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Visit request not found"));

        // Verify ownership of the property
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!visitRequest.getPropertyList().getOwner().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You don't have permission to update this visit request");
        }

        visitRequest.setStatus(status);
        visitRequest.setUpdatedAt(new Date());

        return visitRequestsRepository.save(visitRequest);
    }

    /**
     * Delete visit request
     * 
     * @param id visit request ID
     */
    @Transactional
    public void deleteVisitRequest(Long id) {
        VisitRequests visitRequest = visitRequestsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Visit request not found"));

        // Verify ownership (either the user who created the request or the property owner)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!visitRequest.getUser().getId().equals(user.getId()) && 
            !visitRequest.getPropertyList().getOwner().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You don't have permission to delete this visit request");
        }

        visitRequestsRepository.delete(visitRequest);
    }
}
