package com.backend.roomie.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "swipe_likes")
public class SwipeLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long swiperId;

    @Column(nullable = false)
    private Long swipedId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SwipeStatus status;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public enum SwipeStatus {
        LIKE,
        DISLIKE,
        ACCEPTED
    }
}