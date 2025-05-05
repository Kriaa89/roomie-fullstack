package com.backend.roomie.dtos;

import com.backend.roomie.models.SwipeLike;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SwipeLikeDto {
    private Long id;
    private Long swiperId;
    private Long swipedId;
    private SwipeLike.SwipeStatus status;
    private LocalDateTime timestamp;
    
    // Additional fields for UI display
    private String swiperName;
    private String swiperProfilePicture;
    private String swipedName;
    private String swipedProfilePicture;
}