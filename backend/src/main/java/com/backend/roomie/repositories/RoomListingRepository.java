package com.backend.roomie.repositories;

import com.backend.roomie.models.RoomListing;
import com.backend.roomie.models.RoommateHostProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomListingRepository extends JpaRepository<RoomListing, Long> {
    List<RoomListing> findByHost(RoommateHostProfile host);
    List<RoomListing> findByHostId(Long hostId);
    List<RoomListing> findByCity(String city);
    List<RoomListing> findByPriceLessThanEqual(Double maxPrice);
    List<RoomListing> findByAvailableFromLessThanEqual(LocalDate date);
    List<RoomListing> findByActiveTrue();
    List<RoomListing> findByCityAndActiveTrue(String city);
}