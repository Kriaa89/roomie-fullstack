package com.backend.roomie.dtos;

import com.backend.roomie.models.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * Data Transfer Object for User entity
 * Used to transfer user data between layers without exposing sensitive information
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String profilePicture;
    private String location;
    private Integer age;
    private Boolean emailVerified;
    private Boolean phoneVerified;
    private Boolean idVerified;
    private Date createdAt;
    private Date updatedAt;
    
    // Include roles but not password or other sensitive data
    private List<String> roles;
    
    /**
     * Static method to convert UserRole list to role name list
     * 
     * @param userRoles list of UserRole entities
     * @return list of role names as strings
     */
    public static List<String> convertRoles(List<UserRole> userRoles) {
        if (userRoles == null) {
            return List.of();
        }
        return userRoles.stream()
                .map(role -> role.getRoleType().name())
                .toList();
    }
}