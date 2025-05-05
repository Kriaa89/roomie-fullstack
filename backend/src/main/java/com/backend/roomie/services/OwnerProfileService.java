package com.backend.roomie.services;

import com.backend.roomie.models.AppUser;
import com.backend.roomie.models.OwnerProfile;
import com.backend.roomie.models.Role;
import com.backend.roomie.repositories.AppUserRepository;
import com.backend.roomie.repositories.OwnerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OwnerProfileService {

    private final OwnerProfileRepository ownerProfileRepository;
    private final AppUserRepository appUserRepository;

    public OwnerProfile createOwnerProfile(OwnerProfile ownerProfile, Long userId) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Ensure user has OWNER role
        if (user.getRole() != Role.OWNER) {
            throw new IllegalArgumentException("User must have OWNER role to create an owner profile");
        }

        // Check if profile already exists
        if (ownerProfileRepository.findByAppUser(user).isPresent()) {
            throw new IllegalArgumentException("Owner profile already exists for this user");
        }

        ownerProfile.setAppUser(user);
        return ownerProfileRepository.save(ownerProfile);
    }

    public Optional<OwnerProfile> getOwnerProfileById(Long id) {
        return ownerProfileRepository.findById(id);
    }

    public Optional<OwnerProfile> getOwnerProfileByUserId(Long userId) {
        return ownerProfileRepository.findByAppUserId(userId);
    }

    public List<OwnerProfile> getOwnerProfilesByCity(String city) {
        return ownerProfileRepository.findByCity(city);
    }

    public OwnerProfile updateOwnerProfile(OwnerProfile ownerProfile) {
        // Check if profile exists
        if (!ownerProfileRepository.existsById(ownerProfile.getId())) {
            throw new IllegalArgumentException("Owner profile not found");
        }

        return ownerProfileRepository.save(ownerProfile);
    }

    public void deleteOwnerProfile(Long id) {
        ownerProfileRepository.deleteById(id);
    }

    public List<OwnerProfile> getAllOwnerProfiles() {
        return ownerProfileRepository.findAll();
    }
}
