package com.backend.roomie.controllers;

import com.backend.roomie.dtos.PropertyListingDto;
import com.backend.roomie.dtos.RenterProfileDto;
import com.backend.roomie.dtos.SwipeLikeDto;
import com.backend.roomie.dtos.VisitRequestDto;
import com.backend.roomie.models.AppUser;
import com.backend.roomie.models.RenterProfile;
import com.backend.roomie.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/renter")
@RequiredArgsConstructor
public class RenterProfileController {

    private final RenterProfileService renterProfileService;
    private final AppUserService appUserService;
    private final SwipeLikeService swipeLikeService;
    private final VisitRequestService visitRequestService;
    private final PropertyListingService propertyListingService;

    @GetMapping("/profile")
    @PreAuthorize("hasRole('RENTER')")
    public ResponseEntity<RenterProfileDto> getMyProfile(Authentication authentication) {
        AppUser currentUser = (AppUser) authentication.getPrincipal();
        RenterProfile profile = renterProfileService.getRenterProfileByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        return ResponseEntity.ok(mapToDto(profile));
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('RENTER')")
    public ResponseEntity<RenterProfileDto> updateProfile(
            @RequestBody RenterProfileDto profileDto,
            Authentication authentication) {

        AppUser currentUser = (AppUser) authentication.getPrincipal();
        RenterProfile profile = renterProfileService.getRenterProfileByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        // Update profile fields
        profile.setBio(profileDto.getBio());
        profile.setCity(profileDto.getCity());
        profile.setProfilePictureUrl(profileDto.getProfilePictureUrl());
        profile.setSkillsExpected(profileDto.getSkillsExpected());
        profile.setLifestylePreferences(profileDto.getLifestylePreferences());
        profile.setProfileVisible(profileDto.isProfileVisible());

        RenterProfile updatedProfile = renterProfileService.updateRenterProfile(profile);
        return ResponseEntity.ok(mapToDto(updatedProfile));
    }

    @GetMapping("/profiles/visible")
    public ResponseEntity<List<RenterProfileDto>> getAllVisibleProfiles() {
        List<RenterProfile> profiles = renterProfileService.getAllVisibleRenterProfiles();
        List<RenterProfileDto> profileDtos = profiles.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(profileDtos);
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<RenterProfileDto> getProfileById(@PathVariable Long id) {
        RenterProfile profile = renterProfileService.getRenterProfileById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        // Only return if profile is visible
        if (!profile.isProfileVisible()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(mapToDto(profile));
    }

    @GetMapping("/matches")
    @PreAuthorize("hasRole('RENTER')")
    public ResponseEntity<List<SwipeLikeDto>> getMatches(Authentication authentication) {
        AppUser currentUser = (AppUser) authentication.getPrincipal();
        return ResponseEntity.ok(swipeLikeService.getMatchesForUser(currentUser.getId())
                .stream()
                .map(match -> SwipeLikeDto.builder()
                        .id(match.getId())
                        .swiperId(match.getSwiperId())
                        .swipedId(match.getSwipedId())
                        .status(match.getStatus())
                        .timestamp(match.getTimestamp())
                        .build())
                .collect(Collectors.toList()));
    }

    @GetMapping("/visit-requests")
    @PreAuthorize("hasRole('RENTER')")
    public ResponseEntity<List<VisitRequestDto>> getVisitRequests(Authentication authentication) {
        AppUser currentUser = (AppUser) authentication.getPrincipal();
        return ResponseEntity.ok(visitRequestService.getVisitRequestsBySenderId(currentUser.getId())
                .stream()
                .map(request -> VisitRequestDto.builder()
                        .id(request.getId())
                        .senderId(request.getSenderId())
                        .receiverId(request.getReceiverId())
                        .status(request.getStatus())
                        .requestedDateTime(request.getRequestedDateTime())
                        .message(request.getMessage())
                        .build())
                .collect(Collectors.toList()));
    }

    @GetMapping("/properties")
    @PreAuthorize("hasRole('RENTER')")
    public ResponseEntity<List<PropertyListingDto>> getProperties(Authentication authentication) {
        return ResponseEntity.ok(propertyListingService.getAllActivePropertyListings()
                .stream()
                .map(property -> PropertyListingDto.builder()
                        .id(property.getId())
                        .title(property.getTitle())
                        .description(property.getDescription())
                        .photos(property.getPhotos())
                        .price(property.getPrice())
                        .address(property.getAddress())
                        .city(property.getCity())
                        .availableFrom(property.getAvailableFrom())
                        .active(property.isActive())
                        .ownerId(property.getOwner().getId())
                        .ownerName(property.getOwner().getAppUser().getFirstName() + " " + property.getOwner().getAppUser().getLastName())
                        .ownerProfilePicture(property.getOwner().getProfilePictureUrl())
                        .build())
                .collect(Collectors.toList()));
    }

    private RenterProfileDto mapToDto(RenterProfile profile) {
        return RenterProfileDto.builder()
                .id(profile.getId())
                .appUserId(profile.getAppUser().getId())
                .firstName(profile.getAppUser().getFirstName())
                .lastName(profile.getAppUser().getLastName())
                .email(profile.getAppUser().getEmail())
                .bio(profile.getBio())
                .city(profile.getCity())
                .profilePictureUrl(profile.getProfilePictureUrl())
                .skillsExpected(profile.getSkillsExpected())
                .lifestylePreferences(profile.getLifestylePreferences())
                .profileVisible(profile.isProfileVisible())
                .build();
    }
}
