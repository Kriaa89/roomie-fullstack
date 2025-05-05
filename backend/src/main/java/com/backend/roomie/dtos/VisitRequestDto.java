package com.backend.roomie.dtos;

import com.backend.roomie.models.VisitRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VisitRequestDto {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private VisitRequest.VisitStatus status;
    private LocalDateTime requestedDateTime;
    private String message;
    
    // Additional fields for UI display
    private String senderName;
    private String senderProfilePicture;
    private String receiverName;
    private String receiverProfilePicture;
    private String propertyTitle;
    private Long propertyId;
}