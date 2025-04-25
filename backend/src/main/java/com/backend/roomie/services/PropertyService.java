package com.backend.roomie.services;

import com.backend.roomie.models.PropretyList;
import com.backend.roomie.models.User;
import com.backend.roomie.repositories.PropertyRepository;
import com.backend.roomie.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Service class for property-related operations
 * Contains business logic for managing property listings
 */
@Service
public class PropertyService {

    // Repositories and services for operations
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;

    /**
     * Constructor with dependency injection
     */
    @Autowired
    public PropertyService(PropertyRepository propertyRepository, UserRepository userRepository, FileUploadService fileUploadService) {
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
        this.fileUploadService = fileUploadService;
    }

    /**
     * Create a new property listing
     * @param ownerId ID of the user who owns the property
     * @param property the property details
     * @return the created property
     * @throws Exception if the user is not found
     */
    @Transactional
    public PropretyList createProperty(Long ownerId, PropretyList property) throws Exception {
        // Find the owner by ID
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new Exception("User not found"));

        // Set owner and timestamps
        property.setOwner(owner);
        Date now = new Date();
        property.setCreatedAt(now);
        property.setUpdatedAt(now);

        // Save and return the property
        return propertyRepository.save(property);
    }

    /**
     * Get all properties for a user
     * @param userId ID of the user
     * @return list of properties owned by the user
     * @throws Exception if the user is not found
     */
    public List<PropretyList> getUserProperties(Long userId) throws Exception {
        // Find the owner by ID
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        // Return all properties owned by the user
        return propertyRepository.findByOwner(owner);
    }

    /**
     * Get a property by ID
     * @param propertyId ID of the property
     * @return the property
     * @throws Exception if the property is not found
     */
    public PropretyList getPropertyById(Long propertyId) throws Exception {
        return propertyRepository.findById(propertyId)
                .orElseThrow(() -> new Exception("Property not found"));
    }

    /**
     * Update a property
     * @param propertyId ID of the property to update
     * @param propertyUpdates the updated property details
     * @return the updated property
     * @throws Exception if the property is not found
     */
    @Transactional
    public PropretyList updateProperty(Long propertyId, PropretyList propertyUpdates) throws Exception {
        // Find the existing property
        PropretyList existingProperty = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new Exception("Property not found"));

        // Update fields if provided
        if (propertyUpdates.getName() != null) {
            existingProperty.setName(propertyUpdates.getName());
        }
        if (propertyUpdates.getType() != null) {
            existingProperty.setType(propertyUpdates.getType());
        }
        if (propertyUpdates.getLocation() != null) {
            existingProperty.setLocation(propertyUpdates.getLocation());
        }
        if (propertyUpdates.getPrice() != null) {
            existingProperty.setPrice(propertyUpdates.getPrice());
        }
        if (propertyUpdates.getDescription() != null) {
            existingProperty.setDescription(propertyUpdates.getDescription());
        }
        if (propertyUpdates.getImages() != null) {
            existingProperty.setImages(propertyUpdates.getImages());
        }
        if (propertyUpdates.getAmenities() != null) {
            existingProperty.setAmenities(propertyUpdates.getAmenities());
        }
        if (propertyUpdates.getSurface() != null) {
            existingProperty.setSurface(propertyUpdates.getSurface());
        }

        // Update room counts
        existingProperty.setNumberOfRooms(propertyUpdates.getNumberOfRooms());
        existingProperty.setNumberOfBathrooms(propertyUpdates.getNumberOfBathrooms());
        existingProperty.setNumberOfBedrooms(propertyUpdates.getNumberOfBedrooms());

        if (propertyUpdates.getPropertyRules() != null) {
            existingProperty.setPropertyRules(propertyUpdates.getPropertyRules());
        }

        // Update availability
        existingProperty.setAvailability(propertyUpdates.isAvailability());

        if (propertyUpdates.getAudiance() != null) {
            existingProperty.setAudiance(propertyUpdates.getAudiance());
        }

        // Update timestamp
        existingProperty.setUpdatedAt(new Date());

        // Save and return the updated property
        return propertyRepository.save(existingProperty);
    }

    /**
     * Delete a property
     * @param propertyId ID of the property to delete
     * @throws Exception if the property is not found
     */
    @Transactional
    public void deleteProperty(Long propertyId) throws Exception {
        // Check if property exists
        if (!propertyRepository.existsById(propertyId)) {
            throw new Exception("Property not found");
        }

        // Delete the property
        propertyRepository.deleteById(propertyId);
    }

    /**
     * Upload property images
     * @param propertyId ID of the property
     * @param file the image file to upload
     * @return URL of the uploaded image
     * @throws Exception if the property is not found or the file cannot be uploaded
     */
    public String uploadPropertyImage(Long propertyId, MultipartFile file) throws Exception {
        // Find the property
        PropretyList property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new Exception("Property not found"));

        try {
            // Upload the file and get the URL using FileUploadService
            String imageUrl = fileUploadService.uploadPropertyImage(file);

            // Update property images
            property.setImages(imageUrl);
            property.setUpdatedAt(new Date());
            propertyRepository.save(property);

            return imageUrl;
        } catch (IOException e) {
            throw new Exception("Failed to upload property image: " + e.getMessage());
        }
    }
}
