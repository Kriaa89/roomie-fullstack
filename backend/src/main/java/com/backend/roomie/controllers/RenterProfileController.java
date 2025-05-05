package com.backend.roomie.controllers;

import com.backend.roomie.dtos.RenterProfileDto;
import com.backend.roomie.models.AppUser;
import com.backend.roomie.models.RenterProfile;
import com.backend.roomie.services.AppUserService;
import com.backend.roomie.services.RenterProfileService;
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