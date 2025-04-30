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
import java.util.ArrayList;
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

        // Update profile information
        if (userDTO.getCity() != null) {
            user.setCity(userDTO.getCity());
        }
        if (userDTO.getOccupation() != null) {
            user.setOccupation(userDTO.getOccupation());
        }
        if (userDTO.getBio() != null) {
            user.setBio(userDTO.getBio());
        }

        // Update rental preferences
        if (userDTO.getBudgetMin() != null) {
            user.setBudgetMin(userDTO.getBudgetMin());
        }
        if (userDTO.getBudgetMax() != null) {
            user.setBudgetMax(userDTO.getBudgetMax());
        }
        if (userDTO.getDesiredLocations() != null) {
            user.setDesiredLocations(userDTO.getDesiredLocations());
        }
        if (userDTO.getMoveInDate() != null) {
            user.setMoveInDate(userDTO.getMoveInDate());
        }

        // Update compatibility preferences
        if (userDTO.getPetFriendly() != null) {
            user.setPetFriendly(userDTO.getPetFriendly());
        }
        if (userDTO.getSmoking() != null) {
            user.setSmoking(userDTO.getSmoking());
        }
        if (userDTO.getGenderPreference() != null) {
            user.setGenderPreference(userDTO.getGenderPreference());
        }

        user.setUpdatedAt(new Date());

        // Ensure collections are initialized to prevent NullPointerExceptions
        if (user.getSkills() == null) {
            user.setSkills(new ArrayList<>());
        }
        if (user.getSwipesInitiated() == null) {
            user.setSwipesInitiated(new ArrayList<>());
        }
        if (user.getSwipesReceived() == null) {
            user.setSwipesReceived(new ArrayList<>());
        }
        if (user.getMatchesAsUser1() == null) {
            user.setMatchesAsUser1(new ArrayList<>());
        }
        if (user.getMatchesAsUser2() == null) {
            user.setMatchesAsUser2(new ArrayList<>());
        }
        if (user.getNotifications() == null) {
            user.setNotifications(new ArrayList<>());
        }
        if (user.getRoles() == null) {
            user.setRoles(new ArrayList<>());
        }

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

        // Clear the roles collection and delete from database
        List<UserRole> existingRoles = new ArrayList<>(user.getRoles());
        user.getRoles().clear();
        userRoleRepository.deleteAll(existingRoles);

        // Create new role
        UserRole role = new UserRole();
        role.setUser(user);
        role.setRoleType(roleType);
        role.setCreatedAt(new Date());
        role.setUpdatedAt(new Date());

        // Save role
        userRoleRepository.save(role);

        // Add the new role to the user's roles collection
        user.getRoles().add(role);
        userRepository.save(user);

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
                // Profile Information
                .city(user.getCity())
                .occupation(user.getOccupation())
                .bio(user.getBio())
                // Rental Preferences
                .budgetMin(user.getBudgetMin())
                .budgetMax(user.getBudgetMax())
                .desiredLocations(user.getDesiredLocations())
                .moveInDate(user.getMoveInDate())
                // Compatibility Preferences
                .petFriendly(user.getPetFriendly())
                .smoking(user.getSmoking())
                .genderPreference(user.getGenderPreference())
                .roles(UserDTO.convertRoles(user.getRoles()))
                .build();
    }
}
