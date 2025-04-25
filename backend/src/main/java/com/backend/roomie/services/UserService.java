package com.backend.roomie.services;

import com.backend.roomie.models.User;
import com.backend.roomie.models.UserRole;
import com.backend.roomie.repositories.UserRepository;
import com.backend.roomie.repositories.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    private final FileUploadService fileUploadService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_USER") // Temporary authority (replace with dynamic roles)
                .build();
    }

    /**
     * Register a new user
     * @param user the user to register
     * @return the registered user
     * @throws Exception if the email is already in use or passwords don't match
     */
    @Transactional
    public User registerUser(User user) throws Exception {
        // Check if email already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new Exception("Email already in use");
        }

        // Check if password and password confirmation match
        if (!user.getPassword().equals(user.getPasswordConfirmation())) {
            throw new Exception("Passwords do not match");
        }

        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Set creation and update timestamps
        Date now = new Date();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        // Save user
        return userRepository.save(user);
    }

    /**
     * Login a user
     * @param email the user's email
     * @param password the user's password
     * @return the logged in user if credentials are valid, empty otherwise
     */
    public Optional<User> loginUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return userOpt;
            }
        }

        return Optional.empty();
    }

    /**
     * Assign a role to a user
     * @param userId the user's ID
     * @param roleType the role type to assign
     * @throws Exception if the user is not found
     */
    @Transactional
    public void assignRole(Long userId, UserRole.RoleType roleType) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        // Create a new role
        UserRole role = new UserRole();
        role.setUser(user);
        role.setRoleType(roleType);

        // Set creation and update timestamps
        Date now = new Date();
        role.setCreatedAt(now);
        role.setUpdatedAt(now);

        // Save role
        userRoleRepository.save(role);
    }

    /**
     * Update a user's profile
     * @param userId the user's ID
     * @param userUpdates the updated user details
     * @return the updated user
     * @throws Exception if the user is not found
     */
    @Transactional
    public User updateUserProfile(Long userId, User userUpdates) throws Exception {
        // Find the user by ID
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        // Update fields if provided
        if (userUpdates.getFirstName() != null) {
            existingUser.setFirstName(userUpdates.getFirstName());
        }
        if (userUpdates.getLastName() != null) {
            existingUser.setLastName(userUpdates.getLastName());
        }
        if (userUpdates.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(userUpdates.getPhoneNumber());
        }
        if (userUpdates.getLocation() != null) {
            existingUser.setLocation(userUpdates.getLocation());
        }
        if (userUpdates.getAge() != null) {
            existingUser.setAge(userUpdates.getAge());
        }

        // Update timestamp
        existingUser.setUpdatedAt(new Date());

        // Save and return the updated user
        return userRepository.save(existingUser);
    }

    /**
     * Upload a user's profile picture
     * @param userId the user's ID
     * @param file the profile picture file
     * @return the updated user
     * @throws Exception if the user is not found or the file cannot be uploaded
     */
    @Transactional
    public User uploadProfilePicture(Long userId, MultipartFile file) throws Exception {
        // Find the user by ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        try {
            // Upload the file and get the URL
            String profilePictureUrl = fileUploadService.uploadProfilePicture(file);

            // Update profile picture URL
            user.setProfilePicture(profilePictureUrl);
            user.setUpdatedAt(new Date());

            // Save and return the updated user
            return userRepository.save(user);
        } catch (IOException e) {
            throw new Exception("Failed to upload profile picture: " + e.getMessage());
        }
    }
}
