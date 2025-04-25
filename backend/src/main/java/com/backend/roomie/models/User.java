package com.backend.roomie.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // first name
    @NotEmpty(message = "First name is required")
    @Size(min = 3, message = "First name must be at least 3 characters long")
    private String firstName;

    // last name
    @NotEmpty(message = "Last name is required")
    @Size(min = 3, message = "Last name must be at least 3 characters long")
    private String lastName;

    // email
    @NotEmpty(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    // phone number
    @NotEmpty(message = "Phone number is required")
    private String phoneNumber;

    // profile picture
    private String profilePicture;

    // password
    @NotEmpty(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    // password confirmation
    @Transient
    @NotEmpty(message = "Password confirmation is required")
    @Size(min = 6, message = "Password confirmation must be at least 6 characters long")
    private String passwordConfirmation;

    // - **Location**: current city or coordinates (latitude, longitude)
    @NotEmpty(message = "Location is required")
    private String location;

   // - **Account Verification Status** (email verified, phone verified, ID verified)
    private Boolean emailVerified = false;
    private Boolean phoneVerified = false;
    private Boolean idVerified = false;

    // This will not allow the createdAt column to be updated after creation
    @Column(updatable=false)
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date createdAt;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date updatedAt;

    public List<UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<UserRole> userRoles) {
        this.userRoles = userRoles;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getIdVerified() {
        return idVerified;
    }

    public void setIdVerified(Boolean idVerified) {
        this.idVerified = idVerified;
    }

    public Boolean getPhoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(Boolean phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @OneToMany(mappedBy="user", fetch = FetchType.LAZY)
    private List<UserRole> userRoles;

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="admin_id")
    private Admin admin;

    // User skills (offered and expected)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserSkill> skills;

    // Swipes initiated by this user
    @OneToMany(mappedBy = "swiper", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Swipe> swipesInitiated;

    // Swipes received by this user
    @OneToMany(mappedBy = "targetUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Swipe> swipesReceived;

    // Matches where this user is user1
    @OneToMany(mappedBy = "user1", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Matches> matchesAsUser1;

    // Matches where this user is user2
    @OneToMany(mappedBy = "user2", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Matches> matchesAsUser2;

    // Notifications received by this user
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Notification> notifications;

    // Helper method to get all matches (both as user1 and user2)
    public List<Matches> getAllMatches() {
        List<Matches> allMatches = new ArrayList<>();
        if (matchesAsUser1 != null) {
            allMatches.addAll(matchesAsUser1);
        }
        if (matchesAsUser2 != null) {
            allMatches.addAll(matchesAsUser2);
        }
        return allMatches;
    }

    // Helper method to get all skills offered
    public List<UserSkill> getSkillsOffered() {
        if (skills == null) {
            return new ArrayList<>();
        }
        return skills.stream()
                .filter(skill -> skill.getType() == UserSkill.SkillType.OFFER)
                .collect(java.util.stream.Collectors.toList());
    }

    // Helper method to get all skills expected/wanted to learn
    public List<UserSkill> getSkillsExpected() {
        if (skills == null) {
            return new ArrayList<>();
        }
        return skills.stream()
                .filter(skill -> skill.getType() == UserSkill.SkillType.LEARN)
                .collect(java.util.stream.Collectors.toList());
    }
}
