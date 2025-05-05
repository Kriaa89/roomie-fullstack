package com.backend.roomie.dtos;

import com.backend.roomie.models.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDto {
    private Long id;
    private Long recipientId;
    private String content;
    private Notification.NotificationType type;
    private boolean isRead;
    private LocalDateTime createdAt;
    
    // Additional fields for UI display
    private String relatedEntityId;  // Could be a match ID, visit request ID, etc.
}