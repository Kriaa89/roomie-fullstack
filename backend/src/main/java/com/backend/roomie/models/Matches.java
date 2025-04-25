package com.backend.roomie.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Table(name = "matches")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}
