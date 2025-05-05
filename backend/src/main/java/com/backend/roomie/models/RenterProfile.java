package com.backend.roomie.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "renter_profiles")
public class RenterProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "app_user_id", nullable = false)
    private AppUser appUser;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column
    private String city;

    @Column
    private String profilePictureUrl;

    @Column(columnDefinition = "TEXT")
    private String skillsExpected;

    @Column(columnDefinition = "TEXT")
    private String lifestylePreferences;

    @Column(nullable = false)
    private boolean profileVisible;
}