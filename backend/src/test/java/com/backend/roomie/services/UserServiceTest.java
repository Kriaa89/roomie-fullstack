package com.backend.roomie.services;

import com.backend.roomie.models.AppUser;
import com.backend.roomie.models.Role;
import com.backend.roomie.repositories.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AppUserService appUserService;

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

        // Mock appUserRepository.findById
        when(appUserRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(appUserRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
    }

    @Test
    void testUpdateUserRole() {
        // Arrange
        AppUser userToUpdate = AppUser.builder()
            .id(1L)
            .firstName("Test")
            .lastName("User")
            .email("test@example.com")
            .password("password")
            .role(Role.RENTER)
            .emailVerified(true)
            .phoneNumber("1234567890")
            .build();

        // Mock appUserRepository.existsById
        when(appUserRepository.existsById(1L)).thenReturn(true);

        // Mock appUserRepository.save
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(invocation -> {
            AppUser savedUser = invocation.getArgument(0);
            savedUser.setRole(Role.OWNER);
            return savedUser;
        });

        // Act
        userToUpdate.setRole(Role.OWNER);
        AppUser updatedUser = appUserService.updateUser(userToUpdate);

        // Assert
        assertEquals(Role.OWNER, updatedUser.getRole());
        verify(appUserRepository, times(1)).save(userToUpdate);
    }

    @Test
    void testUpdateUser_ThrowsExceptionIfUserNotFound() {
        // Arrange
        AppUser userToUpdate = AppUser.builder()
            .id(999L) // Non-existent ID
            .firstName("Test")
            .lastName("User")
            .email("test@example.com")
            .password("password")
            .role(Role.RENTER)
            .emailVerified(true)
            .phoneNumber("1234567890")
            .build();

        // Mock appUserRepository.existsById
        when(appUserRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            appUserService.updateUser(userToUpdate);
        });

        // Verify that save was not called
        verify(appUserRepository, never()).save(any(AppUser.class));
    }
}
