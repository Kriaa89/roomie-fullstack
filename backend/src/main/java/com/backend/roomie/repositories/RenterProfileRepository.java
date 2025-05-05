package com.backend.roomie.repositories;

import com.backend.roomie.models.AppUser;
import com.backend.roomie.models.RenterProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RenterProfileRepository extends JpaRepository<RenterProfile, Long> {
    Optional<RenterProfile> findByAppUser(AppUser appUser);
    Optional<RenterProfile> findByAppUserId(Long appUserId);
    List<RenterProfile> findByCity(String city);
    List<RenterProfile> findByProfileVisibleTrue();
}