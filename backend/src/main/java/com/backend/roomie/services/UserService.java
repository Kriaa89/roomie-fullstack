package com.backend.roomie.services;

import com.backend.roomie.models.User;
import com.backend.roomie.models.UserRole;
import com.backend.roomie.repositories.UserRepository;
import com.backend.roomie.repositories.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

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
}
