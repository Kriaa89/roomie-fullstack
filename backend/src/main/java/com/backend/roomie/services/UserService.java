package com.backend.roomie.services;

import com.backend.roomie.dtos.UserDTO;
import com.backend.roomie.models.User;
import com.backend.roomie.models.UserRole;
import com.backend.roomie.repositories.UserRepository;
import com.backend.roomie.repositories.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for handling user operations
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Get current authenticated user
     * 
     * @return UserDTO of current user
     */
    public UserDTO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return convertToDTO(user);
    }

    /**
     * Get user by ID
     * 
     * @param id user ID
     * @return UserDTO
     */
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return convertToDTO(user);
    }

    /**
     * Get all users
     * 
     * @return list of UserDTOs
     */
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update user
     * 
     * @param id user ID
     * @param userDTO user data
     * @return updated UserDTO
     */
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        // Update user fields
        if (userDTO.getFirstName() != null) {
            user.setFirstName(userDTO.getFirstName());
        }
        if (userDTO.getLastName() != null) {
            user.setLastName(userDTO.getLastName());
        }
        if (userDTO.getPhoneNumber() != null) {
            user.setPhoneNumber(userDTO.getPhoneNumber());
        }
        if (userDTO.getProfilePicture() != null) {
            user.setProfilePicture(userDTO.getProfilePicture());
        }
        if (userDTO.getLocation() != null) {
            user.setLocation(userDTO.getLocation());
        }
        if (userDTO.getAge() != null) {
            user.setAge(userDTO.getAge());
        }
        
        user.setUpdatedAt(new Date());
        
        User updatedUser = userRepository.save(user);
        
        return convertToDTO(updatedUser);
    }

    /**
     * Delete user
     * 
     * @param id user ID
     */
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        userRepository.delete(user);
    }

    /**
     * Add role to user
     * 
     * @param userId user ID
     * @param roleType role type
     * @return updated UserDTO
     */
    @Transactional
    public UserDTO addRoleToUser(Long userId, UserRole.RoleType roleType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        // Check if user already has this role
        boolean hasRole = user.getRoles().stream()
                .anyMatch(role -> role.getRoleType() == roleType);
        
        if (hasRole) {
            throw new IllegalArgumentException("User already has this role");
        }
        
        // Create new role
        UserRole role = new UserRole();
        role.setUser(user);
        role.setRoleType(roleType);
        role.setCreatedAt(new Date());
        role.setUpdatedAt(new Date());
        
        // Save role
        userRoleRepository.save(role);
        
        // Refresh user to get updated roles
        User updatedUser = userRepository.findById(userId).orElseThrow();
        
        return convertToDTO(updatedUser);
    }

    /**
     * Remove role from user
     * 
     * @param userId user ID
     * @param roleType role type
     * @return updated UserDTO
     */
    @Transactional
    public UserDTO removeRoleFromUser(Long userId, UserRole.RoleType roleType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        // Find role to remove
        Optional<UserRole> roleToRemove = user.getRoles().stream()
                .filter(role -> role.getRoleType() == roleType)
                .findFirst();
        
        if (roleToRemove.isEmpty()) {
            throw new IllegalArgumentException("User does not have this role");
        }
        
        // Check if it's the only role
        if (user.getRoles().size() == 1) {
            throw new IllegalArgumentException("Cannot remove the only role from user");
        }
        
        // Remove role
        userRoleRepository.delete(roleToRemove.get());
        
        // Refresh user to get updated roles
        User updatedUser = userRepository.findById(userId).orElseThrow();
        
        return convertToDTO(updatedUser);
    }

    /**
     * Change user password
     * 
     * @param userId user ID
     * @param currentPassword current password
     * @param newPassword new password
     * @param confirmPassword confirm new password
     * @return updated UserDTO
     */
    @Transactional
    public UserDTO changePassword(Long userId, String currentPassword, String newPassword, String confirmPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        // Validate current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        
        // Validate new password
        if (newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("New password is required");
        }
        
        // Validate password confirmation
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        
        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(new Date());
        
        User updatedUser = userRepository.save(user);
        
        return convertToDTO(updatedUser);
    }

    /**
     * Convert User entity to UserDTO
     * 
     * @param user User entity
     * @return UserDTO
     */
    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .profilePicture(user.getProfilePicture())
                .location(user.getLocation())
                .age(user.getAge())
                .emailVerified(user.getEmailVerified())
                .phoneVerified(user.getPhoneVerified())
                .idVerified(user.getIdVerified())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .roles(UserDTO.convertRoles(user.getRoles()))
                .build();
    }
}