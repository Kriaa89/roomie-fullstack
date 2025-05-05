package com.backend.roomie.services;

import com.backend.roomie.models.AppUser;
import com.backend.roomie.models.RenterProfile;
import com.backend.roomie.models.Role;
import com.backend.roomie.repositories.AppUserRepository;
import com.backend.roomie.repositories.RenterProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RenterProfileService {

    private final RenterProfileRepository renterProfileRepository;
    private final AppUserRepository appUserRepository;

    public RenterProfile createRenterProfile(RenterProfile renterProfile, Long userId) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        // Ensure user has RENTER role
        if (user.getRole() != Role.RENTER) {
            throw new IllegalArgumentException("User must have RENTER role to create a renter profile");
        }
        
        // Check if profile already exists
        if (renterProfileRepository.findByAppUser(user).isPresent()) {
            throw new IllegalArgumentException("Renter profile already exists for this user");
        }
        
        renterProfile.setAppUser(user);
        return renterProfileRepository.save(renterProfile);
    }

    public Optional<RenterProfile> getRenterProfileById(Long id) {
        return renterProfileRepository.findById(id);
    }

    public Optional<RenterProfile> getRenterProfileByUserId(Long userId) {
        return renterProfileRepository.findByAppUserId(userId);
    }

    public List<RenterProfile> getRenterProfilesByCity(String city) {
        return renterProfileRepository.findByCity(city);
    }

    public List<RenterProfile> getVisibleRenterProfiles() {
        return renterProfileRepository.findByProfileVisibleTrue();
    }

    public RenterProfile updateRenterProfile(RenterProfile renterProfile) {
        // Check if profile exists
        if (!renterProfileRepository.existsById(renterProfile.getId())) {
            throw new IllegalArgumentException("Renter profile not found");
        }
        
        return renterProfileRepository.save(renterProfile);
    }

    public void deleteRenterProfile(Long id) {
        renterProfileRepository.deleteById(id);
    }

    public boolean toggleProfileVisibility(Long profileId) {
        return renterProfileRepository.findById(profileId)
                .map(profile -> {
                    profile.setProfileVisible(!profile.isProfileVisible());
                    renterProfileRepository.save(profile);
                    return profile.isProfileVisible();
                })
                .orElseThrow(() -> new IllegalArgumentException("Renter profile not found"));
    }
}