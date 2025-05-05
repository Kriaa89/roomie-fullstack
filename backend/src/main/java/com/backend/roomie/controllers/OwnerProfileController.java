package com.backend.roomie.controllers;

import com.backend.roomie.dtos.OwnerProfileDto;
import com.backend.roomie.models.AppUser;
import com.backend.roomie.models.OwnerProfile;
import com.backend.roomie.services.AppUserService;
import com.backend.roomie.services.OwnerProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/owner")
@RequiredArgsConstructor
public class OwnerProfileController {

    private final OwnerProfileService ownerProfileService;
    private final AppUserService appUserService;

    @GetMapping("/profile")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<OwnerProfileDto> getMyProfile(Authentication authentication) {
        AppUser currentUser = (AppUser) authentication.getPrincipal();
        OwnerProfile profile = ownerProfileService.getOwnerProfileByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        
        return ResponseEntity.ok(mapToDto(profile));
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<OwnerProfileDto> updateProfile(
            @RequestBody OwnerProfileDto profileDto,
            Authentication authentication) {
        
        AppUser currentUser = (AppUser) authentication.getPrincipal();
        OwnerProfile profile = ownerProfileService.getOwnerProfileByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        
        // Update profile fields
        profile.setCity(profileDto.getCity());
        profile.setProfilePictureUrl(profileDto.getProfilePictureUrl());
        
        OwnerProfile updatedProfile = ownerProfileService.updateOwnerProfile(profile);
        return ResponseEntity.ok(mapToDto(updatedProfile));
    }

    @GetMapping("/profiles")
    public ResponseEntity<List<OwnerProfileDto>> getAllProfiles() {
        List<OwnerProfile> profiles = ownerProfileService.getAllOwnerProfiles();
        List<OwnerProfileDto> profileDtos = profiles.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(profileDtos);
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<OwnerProfileDto> getProfileById(@PathVariable Long id) {
        OwnerProfile profile = ownerProfileService.getOwnerProfileById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        
        return ResponseEntity.ok(mapToDto(profile));
    }

    private OwnerProfileDto mapToDto(OwnerProfile profile) {
        return OwnerProfileDto.builder()
                .id(profile.getId())
                .appUserId(profile.getAppUser().getId())
                .firstName(profile.getAppUser().getFirstName())
                .lastName(profile.getAppUser().getLastName())
                .email(profile.getAppUser().getEmail())
                .city(profile.getCity())
                .profilePictureUrl(profile.getProfilePictureUrl())
                .build();
    }
}