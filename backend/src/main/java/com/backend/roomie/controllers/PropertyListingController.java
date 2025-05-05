package com.backend.roomie.controllers;

import com.backend.roomie.dtos.PropertyListingDto;
import com.backend.roomie.models.AppUser;
import com.backend.roomie.models.OwnerProfile;
import com.backend.roomie.models.PropertyListing;
import com.backend.roomie.services.OwnerProfileService;
import com.backend.roomie.services.PropertyListingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/property-listings")
@RequiredArgsConstructor
public class PropertyListingController {

    private final PropertyListingService propertyListingService;
    private final OwnerProfileService ownerProfileService;

    @GetMapping
    public ResponseEntity<List<PropertyListingDto>> getAllActiveListings() {
        List<PropertyListing> listings = propertyListingService.getAllActivePropertyListings();
        List<PropertyListingDto> listingDtos = listings.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(listingDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyListingDto> getListingById(@PathVariable Long id) {
        PropertyListing listing = propertyListingService.getPropertyListingById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));
        
        return ResponseEntity.ok(mapToDto(listing));
    }

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<PropertyListingDto> createListing(
            @RequestBody PropertyListingDto listingDto,
            Authentication authentication) {
        
        AppUser currentUser = (AppUser) authentication.getPrincipal();
        OwnerProfile ownerProfile = ownerProfileService.getOwnerProfileByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Owner profile not found"));
        
        PropertyListing listing = PropertyListing.builder()
                .title(listingDto.getTitle())
                .description(listingDto.getDescription())
                .photos(listingDto.getPhotos())
                .price(listingDto.getPrice())
                .address(listingDto.getAddress())
                .city(listingDto.getCity())
                .availableFrom(listingDto.getAvailableFrom())
                .active(true)
                .owner(ownerProfile)
                .build();
        
        PropertyListing savedListing = propertyListingService.createPropertyListing(listing);
        return ResponseEntity.ok(mapToDto(savedListing));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<PropertyListingDto> updateListing(
            @PathVariable Long id,
            @RequestBody PropertyListingDto listingDto,
            Authentication authentication) {
        
        AppUser currentUser = (AppUser) authentication.getPrincipal();
        OwnerProfile ownerProfile = ownerProfileService.getOwnerProfileByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Owner profile not found"));
        
        PropertyListing listing = propertyListingService.getPropertyListingById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));
        
        // Verify ownership
        if (!listing.getOwner().getId().equals(ownerProfile.getId())) {
            return ResponseEntity.status(403).build();
        }
        
        // Update fields
        listing.setTitle(listingDto.getTitle());
        listing.setDescription(listingDto.getDescription());
        listing.setPhotos(listingDto.getPhotos());
        listing.setPrice(listingDto.getPrice());
        listing.setAddress(listingDto.getAddress());
        listing.setCity(listingDto.getCity());
        listing.setAvailableFrom(listingDto.getAvailableFrom());
        listing.setActive(listingDto.isActive());
        
        PropertyListing updatedListing = propertyListingService.updatePropertyListing(listing);
        return ResponseEntity.ok(mapToDto(updatedListing));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> deleteListing(
            @PathVariable Long id,
            Authentication authentication) {
        
        AppUser currentUser = (AppUser) authentication.getPrincipal();
        OwnerProfile ownerProfile = ownerProfileService.getOwnerProfileByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Owner profile not found"));
        
        PropertyListing listing = propertyListingService.getPropertyListingById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));
        
        // Verify ownership
        if (!listing.getOwner().getId().equals(ownerProfile.getId())) {
            return ResponseEntity.status(403).build();
        }
        
        propertyListingService.deletePropertyListing(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<PropertyListingDto>> getListingsByOwnerId(@PathVariable Long ownerId) {
        List<PropertyListing> listings = propertyListingService.getPropertyListingsByOwnerId(ownerId);
        List<PropertyListingDto> listingDtos = listings.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(listingDtos);
    }

    @GetMapping("/my-listings")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<PropertyListingDto>> getMyListings(Authentication authentication) {
        AppUser currentUser = (AppUser) authentication.getPrincipal();
        OwnerProfile ownerProfile = ownerProfileService.getOwnerProfileByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Owner profile not found"));
        
        List<PropertyListing> listings = propertyListingService.getPropertyListingsByOwnerId(ownerProfile.getId());
        List<PropertyListingDto> listingDtos = listings.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(listingDtos);
    }

    private PropertyListingDto mapToDto(PropertyListing listing) {
        return PropertyListingDto.builder()
                .id(listing.getId())
                .title(listing.getTitle())
                .description(listing.getDescription())
                .photos(listing.getPhotos())
                .price(listing.getPrice())
                .address(listing.getAddress())
                .city(listing.getCity())
                .availableFrom(listing.getAvailableFrom())
                .active(listing.isActive())
                .ownerId(listing.getOwner().getId())
                .ownerName(listing.getOwner().getAppUser().getFirstName() + " " + listing.getOwner().getAppUser().getLastName())
                .ownerProfilePicture(listing.getOwner().getProfilePictureUrl())
                .build();
    }
}