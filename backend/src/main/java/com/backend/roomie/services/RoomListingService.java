package com.backend.roomie.services;

import com.backend.roomie.models.RoomListing;
import com.backend.roomie.models.RoommateHostProfile;
import com.backend.roomie.repositories.RoomListingRepository;
import com.backend.roomie.repositories.RoommateHostProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomListingService {

    private final RoomListingRepository roomListingRepository;
    private final RoommateHostProfileRepository roommateHostProfileRepository;

    public RoomListing createRoomListing(RoomListing roomListing, Long hostId) {
        RoommateHostProfile host = roommateHostProfileRepository.findById(hostId)
                .orElseThrow(() -> new IllegalArgumentException("Roommate host profile not found"));

        roomListing.setHost(host);
        return roomListingRepository.save(roomListing);
    }

    public Optional<RoomListing> getRoomListingById(Long id) {
        return roomListingRepository.findById(id);
    }

    public List<RoomListing> getRoomListingsByHost(Long hostId) {
        return roomListingRepository.findByHostId(hostId);
    }

    public List<RoomListing> getRoomListingsByCity(String city) {
        return roomListingRepository.findByCity(city);
    }

    public List<RoomListing> getRoomListingsByMaxPrice(Double maxPrice) {
        return roomListingRepository.findByPriceLessThanEqual(maxPrice);
    }

    public List<RoomListing> getRoomListingsAvailableFrom(LocalDate date) {
        return roomListingRepository.findByAvailableFromLessThanEqual(date);
    }

    public List<RoomListing> getActiveRoomListings() {
        return roomListingRepository.findByActiveTrue();
    }

    public List<RoomListing> getActiveRoomListingsByCity(String city) {
        return roomListingRepository.findByCityAndActiveTrue(city);
    }

    public RoomListing updateRoomListing(RoomListing roomListing) {
        // Check if listing exists
        if (!roomListingRepository.existsById(roomListing.getId())) {
            throw new IllegalArgumentException("Room listing not found");
        }

        return roomListingRepository.save(roomListing);
    }

    public void deleteRoomListing(Long id) {
        roomListingRepository.deleteById(id);
    }

    public boolean toggleListingActive(Long listingId) {
        return roomListingRepository.findById(listingId)
                .map(listing -> {
                    listing.setActive(!listing.isActive());
                    roomListingRepository.save(listing);
                    return listing.isActive();
                })
                .orElseThrow(() -> new IllegalArgumentException("Room listing not found"));
    }

    public List<RoomListing> getRoomListingsByHostId(Long hostId) {
        return getRoomListingsByHost(hostId);
    }
}
