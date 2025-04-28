package com.backend.roomie.services;

import com.backend.roomie.models.PropretyList;
import com.backend.roomie.models.User;
import com.backend.roomie.repositories.PropertyRepository;
import com.backend.roomie.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    /**
     * Get all properties
     * 
     * @return list of properties
     */
    public List<PropretyList> getAllProperties() {
        return propertyRepository.findAll();
    }

    /**
     * Get property by ID
     * 
     * @param id property ID
     * @return property
     */
    public PropretyList getPropertyById(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Property not found"));
    }

    /**
     * Get properties by owner
     * 
     * @param ownerId owner ID
     * @return list of properties
     */
    public List<PropretyList> getPropertiesByOwner(Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Owner not found"));
        return propertyRepository.findByOwner(owner);
    }

    /**
     * Get properties by current user
     * 
     * @return list of properties
     */
    public List<PropretyList> getPropertiesByCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return propertyRepository.findByOwner(user);
    }

    /**
     * Get available properties
     * 
     * @return list of available properties
     */
    public List<PropretyList> getAvailableProperties() {
        return propertyRepository.findByAvailability(true);
    }

    /**
     * Get properties by type
     * 
     * @param type property type
     * @return list of properties
     */
    public List<PropretyList> getPropertiesByType(String type) {
        return propertyRepository.findByType(type);
    }

    /**
     * Get properties by location
     * 
     * @param location property location
     * @return list of properties
     */
    public List<PropretyList> getPropertiesByLocation(String location) {
        return propertyRepository.findByLocationContaining(location);
    }

    /**
     * Create property
     * 
     * @param property property data
     * @return created property
     */
    @Transactional
    public PropretyList createProperty(PropretyList property) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        property.setOwner(user);
        property.setCreatedAt(new Date());
        property.setUpdatedAt(new Date());

        return propertyRepository.save(property);
    }

    /**
     * Update property
     * 
     * @param id property ID
     * @param propertyDetails property details
     * @return updated property
     */
    @Transactional
    public PropretyList updateProperty(Long id, Map<String, Object> propertyDetails) {
        PropretyList property = propertyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Property not found"));

        // Verify ownership
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!property.getOwner().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You don't have permission to update this property");
        }

        // Update property fields
        if (propertyDetails.containsKey("name")) {
            property.setName((String) propertyDetails.get("name"));
        }
        if (propertyDetails.containsKey("type")) {
            property.setType((String) propertyDetails.get("type"));
        }
        if (propertyDetails.containsKey("location")) {
            property.setLocation((String) propertyDetails.get("location"));
        }
        if (propertyDetails.containsKey("price")) {
            property.setPrice((String) propertyDetails.get("price"));
        }
        if (propertyDetails.containsKey("description")) {
            property.setDescription((String) propertyDetails.get("description"));
        }
        if (propertyDetails.containsKey("images")) {
            property.setImages((String) propertyDetails.get("images"));
        }
        if (propertyDetails.containsKey("amenities")) {
            property.setAmenities((String) propertyDetails.get("amenities"));
        }
        if (propertyDetails.containsKey("surface")) {
            property.setSurface((String) propertyDetails.get("surface"));
        }
        if (propertyDetails.containsKey("numberOfRooms")) {
            property.setNumberOfRooms((Integer) propertyDetails.get("numberOfRooms"));
        }
        if (propertyDetails.containsKey("numberOfBathrooms")) {
            property.setNumberOfBathrooms((Integer) propertyDetails.get("numberOfBathrooms"));
        }
        if (propertyDetails.containsKey("numberOfBedrooms")) {
            property.setNumberOfBedrooms((Integer) propertyDetails.get("numberOfBedrooms"));
        }
        if (propertyDetails.containsKey("propertyRules")) {
            property.setPropertyRules((String) propertyDetails.get("propertyRules"));
        }
        if (propertyDetails.containsKey("availability")) {
            property.setAvailability((Boolean) propertyDetails.get("availability"));
        }
        if (propertyDetails.containsKey("audiance")) {
            property.setAudiance((String) propertyDetails.get("audiance"));
        }

        property.setUpdatedAt(new Date());

        return propertyRepository.save(property);
    }

    /**
     * Delete property
     * 
     * @param id property ID
     */
    @Transactional
    public void deleteProperty(Long id) {
        PropretyList property = propertyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Property not found"));

        // Verify ownership
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!property.getOwner().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You don't have permission to delete this property");
        }

        propertyRepository.delete(property);
    }

    /**
     * Count properties by owner
     * 
     * @param ownerId owner ID
     * @return count of properties
     */
    public long countPropertiesByOwner(Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Owner not found"));
        return propertyRepository.findByOwner(owner).size();
    }

    /**
     * Count properties by current user
     * 
     * @return count of properties
     */
    public long countPropertiesByCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return propertyRepository.findByOwner(user).size();
    }

    /**
     * Count all properties
     * 
     * @return count of all properties
     */
    public long countAllProperties() {
        return propertyRepository.count();
    }
}
