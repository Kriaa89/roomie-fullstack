package com.backend.roomie.repositories;

import com.backend.roomie.models.AppUser;
import com.backend.roomie.models.RoommateHostProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoommateHostProfileRepository extends JpaRepository<RoommateHostProfile, Long> {
    Optional<RoommateHostProfile> findByAppUser(AppUser appUser);
    Optional<RoommateHostProfile> findByAppUserId(Long appUserId);
    List<RoommateHostProfile> findByCity(String city);
    List<RoommateHostProfile> findByProfileVisibleTrue();
}