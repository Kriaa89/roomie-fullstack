package com.backend.roomie.services;

import com.backend.roomie.dtos.UserDTO;
import com.backend.roomie.models.User;
import com.backend.roomie.models.UserRole;
import com.backend.roomie.repositories.UserRepository;
import com.backend.roomie.repositories.UserRoleRepository;
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
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

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
        userService.addRoleToUser(1L, UserRole.RoleType.OWNER);
        
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
            userService.addRoleToUser(1L, UserRole.RoleType.OWNER);
        });
        
        // Verify that delete was not called
        verify(userRoleRepository, never()).delete(any(UserRole.class));
        
        // Verify that save was not called
        verify(userRoleRepository, never()).save(any(UserRole.class));
    }
}