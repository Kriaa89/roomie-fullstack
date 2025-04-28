package com.backend.roomie.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response object for authentication operations (login and register)
 * Contains the JWT token and user information
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    /**
     * JWT token for authenticated user
     */
    private String token;
    
    /**
     * User ID
     */
    private Long userId;
    
    /**
     * User's email
     */
    private String email;
    
    /**
     * User's first name
     */
    private String firstName;
    
    /**
     * User's last name
     */
    private String lastName;
    
    /**
     * User's roles
     */
    private List<String> roles;
}