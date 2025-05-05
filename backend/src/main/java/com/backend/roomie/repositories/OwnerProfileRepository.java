package com.backend.roomie.repositories;

import com.backend.roomie.models.AppUser;
import com.backend.roomie.models.OwnerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OwnerProfileRepository extends JpaRepository<OwnerProfile, Long> {
    Optional<OwnerProfile> findByAppUser(AppUser appUser);
    Optional<OwnerProfile> findByAppUserId(Long appUserId);
    List<OwnerProfile> findByCity(String city);
}