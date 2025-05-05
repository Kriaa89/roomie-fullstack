package com.backend.roomie.controllers;

import com.backend.roomie.dtos.RoomListingDto;
import com.backend.roomie.models.AppUser;
import com.backend.roomie.models.RoomListing;
import com.backend.roomie.models.RoommateHostProfile;
import com.backend.roomie.services.RoomListingService;
import com.backend.roomie.services.RoommateHostProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/room-listings")
@RequiredArgsConstructor
public class RoomListingController {

    private final RoomListingService roomListingService;
    private final RoommateHostProfileService roommateHostProfileService;

    @GetMapping
    public ResponseEntity<List<RoomListingDto>> getAllActiveListings() {
        List<RoomListing> listings = roomListingService.getActiveRoomListings();
        List<RoomListingDto> listingDtos = listings.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(listingDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomListingDto> getListingById(@PathVariable Long id) {
        RoomListing listing = roomListingService.getRoomListingById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        return ResponseEntity.ok(mapToDto(listing));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROOMMATE_HOST')")
    public ResponseEntity<RoomListingDto> createListing(
            @RequestBody RoomListingDto listingDto,
            Authentication authentication) {

        AppUser currentUser = (AppUser) authentication.getPrincipal();
        RoommateHostProfile hostProfile = roommateHostProfileService.getRoommateHostProfileByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Host profile not found"));

        RoomListing listing = RoomListing.builder()
                .title(listingDto.getTitle())
                .description(listingDto.getDescription())
                .photos(listingDto.getPhotos())
                .price(listingDto.getPrice())
                .address(listingDto.getAddress())
                .city(listingDto.getCity())
                .availableFrom(listingDto.getAvailableFrom())
                .active(true)
                .host(hostProfile)
                .build();

        RoomListing savedListing = roomListingService.createRoomListing(listing, hostProfile.getId());
        return ResponseEntity.ok(mapToDto(savedListing));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROOMMATE_HOST')")
    public ResponseEntity<RoomListingDto> updateListing(
            @PathVariable Long id,
            @RequestBody RoomListingDto listingDto,
            Authentication authentication) {

        AppUser currentUser = (AppUser) authentication.getPrincipal();
        RoommateHostProfile hostProfile = roommateHostProfileService.getRoommateHostProfileByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Host profile not found"));

        RoomListing listing = roomListingService.getRoomListingById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        // Verify ownership
        if (!listing.getHost().getId().equals(hostProfile.getId())) {
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

        RoomListing updatedListing = roomListingService.updateRoomListing(listing);
        return ResponseEntity.ok(mapToDto(updatedListing));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROOMMATE_HOST')")
    public ResponseEntity<Void> deleteListing(
            @PathVariable Long id,
            Authentication authentication) {

        AppUser currentUser = (AppUser) authentication.getPrincipal();
        RoommateHostProfile hostProfile = roommateHostProfileService.getRoommateHostProfileByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Host profile not found"));

        RoomListing listing = roomListingService.getRoomListingById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        // Verify ownership
        if (!listing.getHost().getId().equals(hostProfile.getId())) {
            return ResponseEntity.status(403).build();
        }

        roomListingService.deleteRoomListing(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/host/{hostId}")
    public ResponseEntity<List<RoomListingDto>> getListingsByHostId(@PathVariable Long hostId) {
        List<RoomListing> listings = roomListingService.getRoomListingsByHostId(hostId);
        List<RoomListingDto> listingDtos = listings.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(listingDtos);
    }

    @GetMapping("/my-listings")
    @PreAuthorize("hasRole('ROOMMATE_HOST')")
    public ResponseEntity<List<RoomListingDto>> getMyListings(Authentication authentication) {
        AppUser currentUser = (AppUser) authentication.getPrincipal();
        RoommateHostProfile hostProfile = roommateHostProfileService.getRoommateHostProfileByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Host profile not found"));

        List<RoomListing> listings = roomListingService.getRoomListingsByHostId(hostProfile.getId());
        List<RoomListingDto> listingDtos = listings.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(listingDtos);
    }

    private RoomListingDto mapToDto(RoomListing listing) {
        return RoomListingDto.builder()
                .id(listing.getId())
                .title(listing.getTitle())
                .description(listing.getDescription())
                .photos(listing.getPhotos())
                .price(listing.getPrice())
                .address(listing.getAddress())
                .city(listing.getCity())
                .availableFrom(listing.getAvailableFrom())
                .active(listing.isActive())
                .hostId(listing.getHost().getId())
                .hostName(listing.getHost().getAppUser().getFirstName() + " " + listing.getHost().getAppUser().getLastName())
                .hostProfilePicture(listing.getHost().getProfilePictureUrl())
                .build();
    }
}
