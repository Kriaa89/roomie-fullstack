package com.backend.roomie.controllers;

import com.backend.roomie.config.JwtService;
import com.backend.roomie.dtos.AuthenticationRequest;
import com.backend.roomie.dtos.AuthenticationResponse;
import com.backend.roomie.dtos.RefreshTokenRequest;
import com.backend.roomie.dtos.RegisterRequest;
import com.backend.roomie.models.AppUser;
import com.backend.roomie.models.OwnerProfile;
import com.backend.roomie.models.RenterProfile;
import com.backend.roomie.models.RoommateHostProfile;
import com.backend.roomie.services.AppUserService;
import com.backend.roomie.services.OwnerProfileService;
import com.backend.roomie.services.RenterProfileService;
import com.backend.roomie.services.RoommateHostProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AppUserService appUserService;
    private final RenterProfileService renterProfileService;
    private final RoommateHostProfileService roommateHostProfileService;
    private final OwnerProfileService ownerProfileService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        // Create user
        AppUser user = AppUser.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(request.getPassword())
                .phoneNumber(request.getPhoneNumber())
                .role(request.getRole())
                .emailVerified(true) // Set to true to allow immediate login
                .build();

        AppUser savedUser = appUserService.createUser(user);

        // Create role-specific profile
        switch (savedUser.getRole()) {
            case RENTER:
                RenterProfile renterProfile = RenterProfile.builder()
                        .appUser(savedUser)
                        .profileVisible(true)
                        .build();
                renterProfileService.createRenterProfile(renterProfile, savedUser.getId());
                break;
            case ROOMMATE_HOST:
                RoommateHostProfile hostProfile = RoommateHostProfile.builder()
                        .appUser(savedUser)
                        .profileVisible(true)
                        .build();
                roommateHostProfileService.createRoommateHostProfile(hostProfile, savedUser.getId());
                break;
            case OWNER:
                OwnerProfile ownerProfile = OwnerProfile.builder()
                        .appUser(savedUser)
                        .build();
                ownerProfileService.createOwnerProfile(ownerProfile, savedUser.getId());
                break;
        }

        // Generate token
        String token = jwtService.generateToken(savedUser);

        return ResponseEntity.ok(AuthenticationResponse.builder()
                .token(token)
                .userId(savedUser.getId())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        AppUser user = (AppUser) authentication.getPrincipal();
        String token = jwtService.generateToken(user);

        return ResponseEntity.ok(AuthenticationResponse.builder()
                .token(token)
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .build());
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        // Extract username from token
        String username = jwtService.extractUsername(request.getToken());

        // Get user details
        AppUser user = (AppUser) appUserService.loadUserByUsername(username);

        // Validate token
        if (jwtService.isTokenValid(request.getToken(), user)) {
            // Refresh token
            String refreshedToken = jwtService.refreshToken(request.getToken());

            return ResponseEntity.ok(AuthenticationResponse.builder()
                    .token(refreshedToken)
                    .userId(user.getId())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .build());
        }

        return ResponseEntity.badRequest().build();
    }
}
