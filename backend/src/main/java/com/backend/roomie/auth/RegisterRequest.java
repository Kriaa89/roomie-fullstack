package com.backend.roomie.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request object for user registration
 * Contains all the necessary information to create a new user
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    /**
     * User's first name
     */
    private String firstName;
    
    /**
     * User's last name
     */
    private String lastName;
    
    /**
     * User's email address (will be used as username)
     */
    private String email;
    
    /**
     * User's password
     */
    private String password;
    
    /**
     * Password confirmation to ensure correct password entry
     */
    private String passwordConfirmation;
    
    /**
     * User's phone number
     */
    private String phoneNumber;
    
    /**
     * User's location (city or address)
     */
    private String location;
    
    /**
     * User's age
     */
    private Integer age;
}