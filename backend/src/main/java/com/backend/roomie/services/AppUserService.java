package com.backend.roomie.services;

import com.backend.roomie.models.AppUser;
import com.backend.roomie.models.Role;
import com.backend.roomie.repositories.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppUserService implements UserDetailsService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    public AppUser createUser(AppUser user) {
        // Ensure email is unique
        if (appUserRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        
        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        return appUserRepository.save(user);
    }

    public Optional<AppUser> getUserById(Long id) {
        return appUserRepository.findById(id);
    }

    public Optional<AppUser> getUserByEmail(String email) {
        return appUserRepository.findByEmail(email);
    }

    public List<AppUser> getUsersByRole(Role role) {
        return appUserRepository.findByRole(role);
    }

    public AppUser updateUser(AppUser user) {
        // Check if user exists
        if (!appUserRepository.existsById(user.getId())) {
            throw new IllegalArgumentException("User not found");
        }
        
        // If password is being updated, encode it
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        return appUserRepository.save(user);
    }

    public void deleteUser(Long id) {
        appUserRepository.deleteById(id);
    }

    public boolean verifyEmail(Long userId) {
        return appUserRepository.findById(userId)
                .map(user -> {
                    user.setEmailVerified(true);
                    appUserRepository.save(user);
                    return true;
                })
                .orElse(false);
    }
}