package com.backend.roomie.auth;

import com.backend.roomie.models.User;
import com.backend.roomie.models.UserRole;
import com.backend.roomie.repositories.UserRepository;
import com.backend.roomie.repositories.UserRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private com.backend.roomie.config.JwtService jwtService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setRoles(new ArrayList<>());

        // Mock userRepository.findById
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

        // Mock jwtService.generateToken
        when(jwtService.generateToken(any())).thenReturn("mocked-jwt-token");
    }

    @Test
    void testAddRoleToUser_RemovesExistingRoles() {
        // Arrange
        UserRole existingRole = new UserRole();
        existingRole.setId(1L);
        existingRole.setUser(testUser);
        existingRole.setRoleType(UserRole.RoleType.RENTER);
        existingRole.setCreatedAt(new Date());
        existingRole.setUpdatedAt(new Date());

        testUser.getRoles().add(existingRole);

        // Mock userRoleRepository.save
        when(userRoleRepository.save(any(UserRole.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        authenticationService.addRoleToUser(1L, UserRole.RoleType.OWNER);

        // Assert
        // Verify that delete was called on the existing role
        verify(userRoleRepository, times(1)).delete(existingRole);

        // Verify that save was called with a new role
        verify(userRoleRepository, times(1)).save(any(UserRole.class));
    }

    @Test
    void testAddRoleToUser_ThrowsExceptionIfUserAlreadyHasRole() {
        // Arrange
        UserRole existingRole = new UserRole();
        existingRole.setId(1L);
        existingRole.setUser(testUser);
        existingRole.setRoleType(UserRole.RoleType.OWNER);
        existingRole.setCreatedAt(new Date());
        existingRole.setUpdatedAt(new Date());

        testUser.getRoles().add(existingRole);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            authenticationService.addRoleToUser(1L, UserRole.RoleType.OWNER);
        });

        // Verify that delete was not called
        verify(userRoleRepository, never()).delete(any(UserRole.class));

        // Verify that save was not called
        verify(userRoleRepository, never()).save(any(UserRole.class));
    }
}
