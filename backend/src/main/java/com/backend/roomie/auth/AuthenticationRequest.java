package com.backend.roomie.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request object for user authentication (login)
 * Contains the credentials needed for authentication
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
    /**
     * User's email address used as the username for authentication
     */
    private String email;
    
    /**
     * User's password for authentication
     */
    private String password;
}