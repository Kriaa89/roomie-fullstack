package com.backend.roomie.models;


import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Table(name = "matches")
public class Matches {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // First user in the match
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id")
    private User user1;

    // Second user in the match
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id")
    private User user2;

    // Match status
    @Enumerated(EnumType.STRING)
    private MatchStatus status;

    // Whether notification has been sent to user1
    private Boolean user1Notified = false;

    // Whether notification has been sent to user2
    private Boolean user2Notified = false;

    // Timestamp when the match was created
    @Column(updatable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createdAt;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date updatedAt;

    // Status enum for match status
    public enum MatchStatus {
        ACTIVE,     // Both users have matched and the match is active
        INACTIVE,   // One or both users have unmatched
        EXPIRED     // Match has expired due to inactivity or time limit
    }

    // Lifecycle methods
    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
        this.status = MatchStatus.ACTIVE; // Default status is ACTIVE
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }

    // No-args constructor
    public Matches() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public MatchStatus getStatus() {
        return status;
    }

    public void setStatus(MatchStatus status) {
        this.status = status;
    }

    public Boolean getUser1Notified() {
        return user1Notified;
    }

    public void setUser1Notified(Boolean user1Notified) {
        this.user1Notified = user1Notified;
    }

    public Boolean getUser2Notified() {
        return user2Notified;
    }

    public void setUser2Notified(Boolean user2Notified) {
        this.user2Notified = user2Notified;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
