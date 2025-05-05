package com.backend.roomie.services;

import com.backend.roomie.models.AppUser;
import com.backend.roomie.models.RoommateHostProfile;
import com.backend.roomie.models.Role;
import com.backend.roomie.repositories.AppUserRepository;
import com.backend.roomie.repositories.RoommateHostProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoommateHostProfileService {

    private final RoommateHostProfileRepository roommateHostProfileRepository;
    private final AppUserRepository appUserRepository;

    public RoommateHostProfile createRoommateHostProfile(RoommateHostProfile roommateHostProfile, Long userId) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Ensure user has ROOMMATE_HOST role
        if (user.getRole() != Role.ROOMMATE_HOST) {
            throw new IllegalArgumentException("User must have ROOMMATE_HOST role to create a roommate host profile");
        }

        // Check if profile already exists
        if (roommateHostProfileRepository.findByAppUser(user).isPresent()) {
            throw new IllegalArgumentException("Roommate host profile already exists for this user");
        }

        roommateHostProfile.setAppUser(user);
        return roommateHostProfileRepository.save(roommateHostProfile);
    }

    public Optional<RoommateHostProfile> getRoommateHostProfileById(Long id) {
        return roommateHostProfileRepository.findById(id);
    }

    public Optional<RoommateHostProfile> getRoommateHostProfileByUserId(Long userId) {
        return roommateHostProfileRepository.findByAppUserId(userId);
    }

    public List<RoommateHostProfile> getRoommateHostProfilesByCity(String city) {
        return roommateHostProfileRepository.findByCity(city);
    }

    public List<RoommateHostProfile> getVisibleRoommateHostProfiles() {
        return roommateHostProfileRepository.findByProfileVisibleTrue();
    }

    public RoommateHostProfile updateRoommateHostProfile(RoommateHostProfile roommateHostProfile) {
        // Check if profile exists
        if (!roommateHostProfileRepository.existsById(roommateHostProfile.getId())) {
            throw new IllegalArgumentException("Roommate host profile not found");
        }

        return roommateHostProfileRepository.save(roommateHostProfile);
    }

    public void deleteRoommateHostProfile(Long id) {
        roommateHostProfileRepository.deleteById(id);
    }

    public boolean toggleProfileVisibility(Long profileId) {
        return roommateHostProfileRepository.findById(profileId)
                .map(profile -> {
                    profile.setProfileVisible(!profile.isProfileVisible());
                    roommateHostProfileRepository.save(profile);
                    return profile.isProfileVisible();
                })
                .orElseThrow(() -> new IllegalArgumentException("Roommate host profile not found"));
    }

    public List<RoommateHostProfile> getAllVisibleRoommateHostProfiles() {
        return getVisibleRoommateHostProfiles();
    }
}
