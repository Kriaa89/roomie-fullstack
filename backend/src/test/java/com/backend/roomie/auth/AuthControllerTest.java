package com.backend.roomie.auth;

import com.backend.roomie.config.JwtService;
import com.backend.roomie.controllers.AuthController;
import com.backend.roomie.dtos.AuthenticationRequest;
import com.backend.roomie.dtos.AuthenticationResponse;
import com.backend.roomie.dtos.RegisterRequest;
import com.backend.roomie.models.AppUser;
import com.backend.roomie.models.Role;
import com.backend.roomie.repositories.AppUserRepository;
import com.backend.roomie.services.AppUserService;
import com.backend.roomie.services.OwnerProfileService;
import com.backend.roomie.services.RenterProfileService;
import com.backend.roomie.services.RoommateHostProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private AppUserService appUserService;

    @Mock
    private RenterProfileService renterProfileService;

    @Mock
    private RoommateHostProfileService roommateHostProfileService;

    @Mock
    private OwnerProfileService ownerProfileService;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthController authController;

    private AppUser testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create test user
        testUser = AppUser.builder()
            .id(1L)
            .firstName("Test")
            .lastName("User")
            .email("test@example.com")
            .password("password")
            .role(Role.RENTER)
            .emailVerified(true)
            .phoneNumber("1234567890")
            .build();

        // Mock appUserService.getUserById
        when(appUserService.getUserById(1L)).thenReturn(Optional.of(testUser));
        
        // Mock appUserService.createUser
        when(appUserService.createUser(any(AppUser.class))).thenReturn(testUser);

        // Mock jwtService.generateToken
        when(jwtService.generateToken(any())).thenReturn("mocked-jwt-token");
        
        // Mock Authentication
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
    }

    @Test
    void testRegister() {
        // Arrange
        RegisterRequest request = RegisterRequest.builder()
            .firstName("Test")
            .lastName("User")
            .email("test@example.com")
            .password("password")
            .phoneNumber("1234567890")
            .role(Role.RENTER)
            .build();

        // Act
        ResponseEntity<AuthenticationResponse> response = authController.register(request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("mocked-jwt-token", response.getBody().getToken());
        assertEquals(testUser.getId(), response.getBody().getUserId());
        assertEquals(testUser.getFirstName(), response.getBody().getFirstName());
        assertEquals(testUser.getLastName(), response.getBody().getLastName());
        assertEquals(testUser.getEmail(), response.getBody().getEmail());
        assertEquals(testUser.getRole(), response.getBody().getRole());

        // Verify that createUser was called
        verify(appUserService, times(1)).createUser(any(AppUser.class));
    }

    @Test
    void testLogin() {
        // Arrange
        AuthenticationRequest request = AuthenticationRequest.builder()
            .email("test@example.com")
            .password("password")
            .build();

        // Act
        ResponseEntity<AuthenticationResponse> response = authController.login(request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("mocked-jwt-token", response.getBody().getToken());
        assertEquals(testUser.getId(), response.getBody().getUserId());
        assertEquals(testUser.getFirstName(), response.getBody().getFirstName());
        assertEquals(testUser.getLastName(), response.getBody().getLastName());
        assertEquals(testUser.getEmail(), response.getBody().getEmail());
        assertEquals(testUser.getRole(), response.getBody().getRole());

        // Verify that authenticate was called
        verify(authenticationManager, times(1)).authenticate(any());
    }
}