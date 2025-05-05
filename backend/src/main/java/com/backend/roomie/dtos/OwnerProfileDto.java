package com.backend.roomie.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OwnerProfileDto {
    private Long id;
    private Long appUserId;
    private String firstName;
    private String lastName;
    private String email;
    private String city;
    private String profilePictureUrl;
}