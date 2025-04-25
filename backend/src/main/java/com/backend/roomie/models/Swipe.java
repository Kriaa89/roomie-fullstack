package com.backend.roomie.models;


import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Table(name = "swipes")
public class Swipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The user who performed the swipe
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "swiper_id")
    private User swiper;

    // The user who was swiped on (for user-to-user swiping)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id")
    private User targetUser;

    // The property that was swiped on (for user-to-property swiping)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id")
    private PropretyList propertyList;

    // Status of the swipe (LEFT or RIGHT)
    @Enumerated(EnumType.STRING)
    private Status status;

    // Timestamp when the swipe was created
    @Column(updatable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createdAt;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date updatedAt;

    // Status enum for swipe direction
    public enum Status {
        LEFT,  // Dislike
        RIGHT  // Like
    }

    // Lifecycle methods
    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }

    // No-args constructor
    public Swipe() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSwiper() {
        return swiper;
    }

    public void setSwiper(User swiper) {
        this.swiper = swiper;
    }

    public User getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(User targetUser) {
        this.targetUser = targetUser;
    }

    public PropretyList getPropertyList() {
        return propertyList;
    }

    public void setPropertyList(PropretyList propertyList) {
        this.propertyList = propertyList;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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
