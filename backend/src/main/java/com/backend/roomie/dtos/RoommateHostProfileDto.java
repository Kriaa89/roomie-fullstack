package com.backend.roomie.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoommateHostProfileDto {
    private Long id;
    private Long appUserId;
    private String firstName;
    private String lastName;
    private String email;
    private String bio;
    private String city;
    private String profilePictureUrl;
    private String skillsOffered;
    private String skillsExpected;
    private String lifestylePreferences;
    private boolean profileVisible;
}