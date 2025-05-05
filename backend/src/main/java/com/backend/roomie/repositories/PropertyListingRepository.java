package com.backend.roomie.repositories;

import com.backend.roomie.models.OwnerProfile;
import com.backend.roomie.models.PropertyListing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PropertyListingRepository extends JpaRepository<PropertyListing, Long> {
    List<PropertyListing> findByOwner(OwnerProfile owner);
    List<PropertyListing> findByOwnerId(Long ownerId);
    List<PropertyListing> findByCity(String city);
    List<PropertyListing> findByPriceLessThanEqual(Double maxPrice);
    List<PropertyListing> findByAvailableFromLessThanEqual(LocalDate date);
    List<PropertyListing> findByActiveTrue();
    List<PropertyListing> findByCityAndActiveTrue(String city);
}