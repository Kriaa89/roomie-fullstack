package com.backend.roomie.services;

import com.backend.roomie.models.OwnerProfile;
import com.backend.roomie.models.PropertyListing;
import com.backend.roomie.repositories.OwnerProfileRepository;
import com.backend.roomie.repositories.PropertyListingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PropertyListingService {

    private final PropertyListingRepository propertyListingRepository;
    private final OwnerProfileRepository ownerProfileRepository;

    public PropertyListing createPropertyListing(PropertyListing propertyListing, Long ownerId) {
        OwnerProfile owner = ownerProfileRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Owner profile not found"));

        propertyListing.setOwner(owner);
        return propertyListingRepository.save(propertyListing);
    }

    public Optional<PropertyListing> getPropertyListingById(Long id) {
        return propertyListingRepository.findById(id);
    }

    public List<PropertyListing> getPropertyListingsByOwner(Long ownerId) {
        return propertyListingRepository.findByOwnerId(ownerId);
    }

    public List<PropertyListing> getPropertyListingsByCity(String city) {
        return propertyListingRepository.findByCity(city);
    }

    public List<PropertyListing> getPropertyListingsByMaxPrice(Double maxPrice) {
        return propertyListingRepository.findByPriceLessThanEqual(maxPrice);
    }

    public List<PropertyListing> getPropertyListingsAvailableFrom(LocalDate date) {
        return propertyListingRepository.findByAvailableFromLessThanEqual(date);
    }

    public List<PropertyListing> getActivePropertyListings() {
        return propertyListingRepository.findByActiveTrue();
    }

    public List<PropertyListing> getActivePropertyListingsByCity(String city) {
        return propertyListingRepository.findByCityAndActiveTrue(city);
    }

    public PropertyListing updatePropertyListing(PropertyListing propertyListing) {
        // Check if listing exists
        if (!propertyListingRepository.existsById(propertyListing.getId())) {
            throw new IllegalArgumentException("Property listing not found");
        }

        return propertyListingRepository.save(propertyListing);
    }

    public void deletePropertyListing(Long id) {
        propertyListingRepository.deleteById(id);
    }

    public boolean toggleListingActive(Long listingId) {
        return propertyListingRepository.findById(listingId)
                .map(listing -> {
                    listing.setActive(!listing.isActive());
                    propertyListingRepository.save(listing);
                    return listing.isActive();
                })
                .orElseThrow(() -> new IllegalArgumentException("Property listing not found"));
    }

    public List<PropertyListing> getAllActivePropertyListings() {
        return getActivePropertyListings();
    }

    public PropertyListing createPropertyListing(PropertyListing propertyListing) {
        if (propertyListing.getOwner() == null || propertyListing.getOwner().getId() == null) {
            throw new IllegalArgumentException("Property listing must have an owner with a valid ID");
        }
        return createPropertyListing(propertyListing, propertyListing.getOwner().getId());
    }

    public List<PropertyListing> getPropertyListingsByOwnerId(Long ownerId) {
        return getPropertyListingsByOwner(ownerId);
    }
}
