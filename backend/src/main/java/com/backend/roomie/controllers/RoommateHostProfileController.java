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
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roommate-host")
@RequiredArgsConstructor
public class RoommateHostProfileController {

    private final RoommateHostProfileService roommateHostProfileService;
    private final AppUserService appUserService;

    @PostMapping("/profile")
    @PreAuthorize("hasRole('ROOMMATE_HOST')")
    public ResponseEntity<RoommateHostProfileDto> createProfile(
            @RequestBody RoommateHostProfileDto profileDto,
            Authentication authentication) {

        AppUser currentUser = (AppUser) authentication.getPrincipal();

        // Convert List<String> to String for storage
        String skillsOffered = profileDto.getSkillsOffered() != null ? 
                String.join(",", profileDto.getSkillsOffered()) : "";

        String skillsExpected = profileDto.getSkillsExpected() != null ? 
                String.join(",", profileDto.getSkillsExpected()) : "";

        String lifestylePreferences = profileDto.getLifestylePreferences() != null ? 
                String.join(",", profileDto.getLifestylePreferences()) : "";

        // Create a new profile from the DTO
        RoommateHostProfile profile = RoommateHostProfile.builder()
                .bio(profileDto.getBio())
                .city(profileDto.getCity())
                .profilePictureUrl(profileDto.getProfilePictureUrl())
                .skillsOffered(skillsOffered)
                .skillsExpected(skillsExpected)
                .lifestylePreferences(lifestylePreferences)
                .profileVisible(profileDto.isProfileVisible())
                .build();

        // Save the profile
        RoommateHostProfile createdProfile = roommateHostProfileService.createRoommateHostProfile(profile, currentUser.getId());

        return ResponseEntity.ok(mapToDto(createdProfile));
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('ROOMMATE_HOST')")
    public ResponseEntity<RoommateHostProfileDto> getMyProfile(Authentication authentication) {
        AppUser currentUser = (AppUser) authentication.getPrincipal();
        RoommateHostProfile profile = roommateHostProfileService.getRoommateHostProfileByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        return ResponseEntity.ok(mapToDto(profile));
    }

    @GetMapping("/profile/{id}")
    @PreAuthorize("hasAnyRole('ROOMMATE_HOST', 'RENTER')")
    public ResponseEntity<RoommateHostProfileDto> getProfileById(@PathVariable Long id) {
        RoommateHostProfile profile = roommateHostProfileService.getRoommateHostProfileById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        // Only return if profile is visible
        if (!profile.isProfileVisible()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(mapToDto(profile));
    }

    @GetMapping("/profile/user/{userId}")
    @PreAuthorize("hasAnyRole('ROOMMATE_HOST', 'RENTER')")
    public ResponseEntity<RoommateHostProfileDto> getProfileByUserId(@PathVariable Long userId) {
        Optional<RoommateHostProfile> profileOpt = roommateHostProfileService.getRoommateHostProfileByUserId(userId);

        if (profileOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(mapToDto(profileOpt.get()));
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

        // Convert List<String> to comma-separated String for storage
        if (profileDto.getSkillsOffered() != null) {
            profile.setSkillsOffered(String.join(",", profileDto.getSkillsOffered()));
        }

        if (profileDto.getSkillsExpected() != null) {
            profile.setSkillsExpected(String.join(",", profileDto.getSkillsExpected()));
        }

        if (profileDto.getLifestylePreferences() != null) {
            profile.setLifestylePreferences(String.join(",", profileDto.getLifestylePreferences()));
        }

        profile.setProfileVisible(profileDto.isProfileVisible());

        RoommateHostProfile updatedProfile = roommateHostProfileService.updateRoommateHostProfile(profile);
        return ResponseEntity.ok(mapToDto(updatedProfile));
    }

    @GetMapping("/profiles/visible")
    @PreAuthorize("hasAnyRole('ROOMMATE_HOST', 'RENTER')")
    public ResponseEntity<List<RoommateHostProfileDto>> getAllVisibleProfiles() {
        List<RoommateHostProfile> profiles = roommateHostProfileService.getAllVisibleRoommateHostProfiles();
        List<RoommateHostProfileDto> profileDtos = profiles.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(profileDtos);
    }

    @PutMapping("/profile/{userId}")
    @PreAuthorize("hasRole('ROOMMATE_HOST')")
    public ResponseEntity<RoommateHostProfileDto> updateProfileByUserId(
            @PathVariable Long userId,
            @RequestBody RoommateHostProfileDto profileDto,
            Authentication authentication) {

        AppUser currentUser = (AppUser) authentication.getPrincipal();

        // Check if the user is updating their own profile
        if (!currentUser.getId().equals(userId)) {
            return ResponseEntity.status(403).build();
        }

        Optional<RoommateHostProfile> profileOpt = roommateHostProfileService.getRoommateHostProfileByUserId(userId);

        if (profileOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        RoommateHostProfile profile = profileOpt.get();

        // Update profile fields
        profile.setBio(profileDto.getBio());
        profile.setCity(profileDto.getCity());
        profile.setProfilePictureUrl(profileDto.getProfilePictureUrl());

        // Convert List<String> to String for storage
        if (profileDto.getSkillsOffered() != null) {
            String skillsOffered = String.join(",", profileDto.getSkillsOffered());
            profile.setSkillsOffered(skillsOffered);
        }

        if (profileDto.getSkillsExpected() != null) {
            String skillsExpected = String.join(",", profileDto.getSkillsExpected());
            profile.setSkillsExpected(skillsExpected);
        }

        if (profileDto.getLifestylePreferences() != null) {
            String lifestylePreferences = String.join(",", profileDto.getLifestylePreferences());
            profile.setLifestylePreferences(lifestylePreferences);
        }

        profile.setProfileVisible(profileDto.isProfileVisible());

        RoommateHostProfile updatedProfile = roommateHostProfileService.updateRoommateHostProfile(profile);
        return ResponseEntity.ok(mapToDto(updatedProfile));
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
                .skillsOffered(profile.getSkillsOffered() != null && !profile.getSkillsOffered().isEmpty() ? 
                        java.util.Arrays.asList(profile.getSkillsOffered().split(",")) : 
                        java.util.Collections.emptyList())
                .skillsExpected(profile.getSkillsExpected() != null && !profile.getSkillsExpected().isEmpty() ? 
                        java.util.Arrays.asList(profile.getSkillsExpected().split(",")) : 
                        java.util.Collections.emptyList())
                .lifestylePreferences(profile.getLifestylePreferences() != null && !profile.getLifestylePreferences().isEmpty() ? 
                        java.util.Arrays.asList(profile.getLifestylePreferences().split(",")) : 
                        java.util.Collections.emptyList())
                .profileVisible(profile.isProfileVisible())
                .build();
    }
}
