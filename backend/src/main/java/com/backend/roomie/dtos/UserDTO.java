package com.backend.roomie.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for transferring user information.
 *
 * This class represents a user with fields for ID, full name, email, and role.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id; // Unique identifier for the user
    private String fullName; // First name of the user
    private String email; // Email address of the user
    private String role; // Role of the user (e.g., ADMIN, USER)
}
