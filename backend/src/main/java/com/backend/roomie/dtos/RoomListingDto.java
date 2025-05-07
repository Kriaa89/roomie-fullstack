package com.backend.roomie.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomListingDto {
    private Long id;
    private String title;
    private String description;
    private List<String> photos;
    private String photosString; // Added for frontend compatibility
    private Double price;
    private String address;
    private String city;
    private LocalDate availableFrom;
    private boolean active;
    private boolean isAvailable; // Added for frontend compatibility
    private Long hostId;
    private String hostName;
    private String hostProfilePicture;
}
