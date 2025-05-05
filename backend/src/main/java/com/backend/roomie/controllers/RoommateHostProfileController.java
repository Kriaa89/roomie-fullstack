package com.backend.roomie.controllers;

import com.backend.roomie.dtos.RoommateHostProfileDto;
import com.backend.roomie.models.AppUser;
import com.backend.roomie.models.RoommateHostProfile;
import com.backend.roomie.services.AppUserService;
import com.backend.roomie.services.RoommateHostProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roommate-host")
@RequiredArgsConstructor
public class RoommateHostProfileController {

    private final RoommateHostProfileService roommateHostProfileService;
    private final AppUserService appUserService;

    @GetMapping("/profile")
    @PreAuthorize("hasRole('ROOMMATE_HOST')")
    public ResponseEntity<RoommateHostProfileDto> getMyProfile(Authentication authentication) {
        AppUser currentUser = (AppUser) authentication.getPrincipal();
        RoommateHostProfile profile = roommateHostProfileService.getRoommateHostProfileByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        
        return ResponseEntity.ok(mapToDto(profile));
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('ROOMMATE_HOST')")
    public ResponseEntity<RoommateHostProfileDto> updateProfile(
            @RequestBody RoommateHostProfileDto profileDto,
            Authentication authentication) {
        
        AppUser currentUser = (AppUser) authentication.getPrincipal();
        RoommateHostProfile profile = roommateHostProfileService.getRoommateHostProfileByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        
        // Update profile fields
        profile.setBio(profileDto.getBio());
        profile.setCity(profileDto.getCity());
        profile.setProfilePictureUrl(profileDto.getProfilePictureUrl());
        profile.setSkillsOffered(profileDto.getSkillsOffered());
        profile.setSkillsExpected(profileDto.getSkillsExpected());
        profile.setLifestylePreferences(profileDto.getLifestylePreferences());
        profile.setProfileVisible(profileDto.isProfileVisible());
        
        RoommateHostProfile updatedProfile = roommateHostProfileService.updateRoommateHostProfile(profile);
        return ResponseEntity.ok(mapToDto(updatedProfile));
    }

    @GetMapping("/profiles/visible")
    public ResponseEntity<List<RoommateHostProfileDto>> getAllVisibleProfiles() {
        List<RoommateHostProfile> profiles = roommateHostProfileService.getAllVisibleRoommateHostProfiles();
        List<RoommateHostProfileDto> profileDtos = profiles.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(profileDtos);
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<RoommateHostProfileDto> getProfileById(@PathVariable Long id) {
        RoommateHostProfile profile = roommateHostProfileService.getRoommateHostProfileById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        
        // Only return if profile is visible
        if (!profile.isProfileVisible()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(mapToDto(profile));
    }

    private RoommateHostProfileDto mapToDto(RoommateHostProfile profile) {
        return RoommateHostProfileDto.builder()
                .id(profile.getId())
                .appUserId(profile.getAppUser().getId())
                .firstName(profile.getAppUser().getFirstName())
                .lastName(profile.getAppUser().getLastName())
                .email(profile.getAppUser().getEmail())
                .bio(profile.getBio())
                .city(profile.getCity())
                .profilePictureUrl(profile.getProfilePictureUrl())
                .skillsOffered(profile.getSkillsOffered())
                .skillsExpected(profile.getSkillsExpected())
                .lifestylePreferences(profile.getLifestylePreferences())
                .profileVisible(profile.isProfileVisible())
                .build();
    }
}